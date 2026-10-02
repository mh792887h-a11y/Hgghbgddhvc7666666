package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.backup.BackupManager
import com.example.data.local.AppDatabase
import com.example.data.model.ChecklistItem
import com.example.data.model.DefaultCategories
import com.example.data.model.DrawingStroke
import com.example.data.model.Note
import com.example.data.model.NoteSnapshot
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Archive : Screen()
    object Trash : Screen()
    object Stats : Screen()
    object BackupSecurity : Screen()
    data class Editor(val noteId: Long? = null, val isChecklist: Boolean = false) : Screen()
}

enum class NoteFilterType(val label: String) {
    ALL("الكل"),
    TEXT_ONLY("ملاحظات نصية"),
    CHECKLIST_ONLY("قوائم مهام"),
    FAVORITES("المفضلة"),
    HAS_DRAWING("رسومات يدوية"),
    HAS_AUDIO("تسجيلات صوتية"),
    LOCKED("ملاحظات مقفلة")
}

enum class SortOption(val label: String) {
    DATE_MODIFIED_DESC("تاريخ آخر تعديل"),
    DATE_CREATED_DESC("تاريخ الإنشاء"),
    TITLE_ASC("الاسم أبجدياً")
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    private val prefs = application.getSharedPreferences("pronotes_prefs", Context.MODE_PRIVATE)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = NoteRepository(database.noteDao())
        viewModelScope.launch {
            repository.seedInitialNotesIfEmpty()
        }
    }

    // Master PIN
    private val _appMasterPin = MutableStateFlow(prefs.getString("master_pin", "") ?: "")
    val appMasterPin: StateFlow<String> = _appMasterPin.asStateFlow()

    // Session unlocked note IDs
    private val _unlockedNotesSet = MutableStateFlow<Set<Long>>(emptySet())
    val unlockedNotesSet: StateFlow<Set<Long>> = _unlockedNotesSet.asStateFlow()

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    // Filter & Search State
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow(DefaultCategories.ALL)
    val filterType = MutableStateFlow(NoteFilterType.ALL)
    val sortOption = MutableStateFlow(SortOption.DATE_MODIFIED_DESC)
    val isGridView = MutableStateFlow(true)

    // User custom categories
    private val _customCategories = MutableStateFlow<List<String>>(
        DefaultCategories.list.map { it.name }
    )
    val customCategories: StateFlow<List<String>> = _customCategories.asStateFlow()

    // Active raw notes from DB
    val rawActiveNotes = repository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedNotes = repository.archivedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashedNotes = repository.trashedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered & Sorted Active Notes
    val filteredActiveNotes: StateFlow<List<Note>> = combine(
        rawActiveNotes,
        searchQuery,
        selectedCategory,
        filterType,
        sortOption
    ) { notes, query, category, type, sort ->
        var result = notes

        // Category Filter
        if (category != DefaultCategories.ALL) {
            result = result.filter { it.category.equals(category, ignoreCase = true) }
        }

        // Type Filter
        result = when (type) {
            NoteFilterType.ALL -> result
            NoteFilterType.TEXT_ONLY -> result.filter { !it.isChecklist }
            NoteFilterType.CHECKLIST_ONLY -> result.filter { it.isChecklist }
            NoteFilterType.FAVORITES -> result.filter { it.isFavorite }
            NoteFilterType.HAS_DRAWING -> result.filter { it.hasDrawing() }
            NoteFilterType.HAS_AUDIO -> result.filter { it.hasAudio() }
            NoteFilterType.LOCKED -> result.filter { it.isLocked }
        }

        // Search Query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter { note ->
                note.title.lowercase().contains(q) ||
                note.content.lowercase().contains(q) ||
                note.category.lowercase().contains(q) ||
                note.tags.lowercase().contains(q) ||
                (note.isChecklist && note.getChecklistItems().any { it.text.lowercase().contains(q) })
            }
        }

        // Sort
        when (sort) {
            SortOption.DATE_MODIFIED_DESC -> result.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenByDescending { it.updatedAt }
            )
            SortOption.DATE_CREATED_DESC -> result.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenByDescending { it.createdAt }
            )
            SortOption.TITLE_ASC -> result.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenBy { it.title.ifBlank { "zzz" } }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Editor Form State
    private val _editingNoteId = MutableStateFlow<Long?>(null)
    val editingNoteId: StateFlow<Long?> = _editingNoteId.asStateFlow()

    val editorTitle = MutableStateFlow("")
    val editorContent = MutableStateFlow("")
    val editorCategory = MutableStateFlow(DefaultCategories.GENERAL)
    val editorColorHex = MutableStateFlow("#00000000")
    val editorIsPinned = MutableStateFlow(false)
    val editorIsFavorite = MutableStateFlow(false)
    val editorIsLocked = MutableStateFlow(false)
    val editorIsChecklist = MutableStateFlow(false)
    val editorChecklistItems = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val editorDrawingStrokes = MutableStateFlow<List<DrawingStroke>>(emptyList())
    val editorAudioPath = MutableStateFlow("")
    val editorAudioDuration = MutableStateFlow(0L)
    val editorTags = MutableStateFlow("")
    val editorSnapshots = MutableStateFlow<List<NoteSnapshot>>(emptyList())
    val editorCreatedAt = MutableStateFlow<Long>(System.currentTimeMillis())
    val editorUpdatedAt = MutableStateFlow<Long>(System.currentTimeMillis())

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_currentScreen.value is Screen.Editor) {
            saveCurrentNote()
        }
        return if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            true
        } else if (_currentScreen.value !is Screen.Home) {
            _currentScreen.value = Screen.Home
            true
        } else {
            false
        }
    }

    fun openNewNote(isChecklist: Boolean = false, defaultCat: String = DefaultCategories.GENERAL) {
        _editingNoteId.value = null
        editorTitle.value = ""
        editorContent.value = ""
        editorCategory.value = if (selectedCategory.value != DefaultCategories.ALL) selectedCategory.value else defaultCat
        editorColorHex.value = "#00000000"
        editorIsPinned.value = false
        editorIsFavorite.value = false
        editorIsLocked.value = false
        editorIsChecklist.value = isChecklist
        editorDrawingStrokes.value = emptyList()
        editorAudioPath.value = ""
        editorAudioDuration.value = 0L
        editorTags.value = ""
        editorSnapshots.value = emptyList()
        val now = System.currentTimeMillis()
        editorCreatedAt.value = now
        editorUpdatedAt.value = now

        if (isChecklist) {
            editorChecklistItems.value = listOf(ChecklistItem(text = "", isChecked = false))
        } else {
            editorChecklistItems.value = emptyList()
        }

        navigateTo(Screen.Editor(noteId = null, isChecklist = isChecklist))
    }

    fun openTemplate(template: com.example.data.model.NoteTemplate) {
        _editingNoteId.value = null
        editorTitle.value = template.title
        editorContent.value = template.content
        editorCategory.value = template.category
        editorColorHex.value = template.colorHex
        editorIsPinned.value = false
        editorIsFavorite.value = false
        editorIsLocked.value = false
        editorIsChecklist.value = template.isChecklist
        editorDrawingStrokes.value = emptyList()
        editorAudioPath.value = ""
        editorAudioDuration.value = 0L
        editorTags.value = template.category
        editorSnapshots.value = emptyList()
        val now = System.currentTimeMillis()
        editorCreatedAt.value = now
        editorUpdatedAt.value = now

        if (template.isChecklist) {
            editorChecklistItems.value = template.checklistItems
        } else {
            editorChecklistItems.value = emptyList()
        }

        navigateTo(Screen.Editor(noteId = null, isChecklist = template.isChecklist))
    }

    fun openEditNote(note: Note) {
        _editingNoteId.value = note.id
        editorTitle.value = note.title
        editorContent.value = note.content
        editorCategory.value = note.category
        editorColorHex.value = note.colorHex
        editorIsPinned.value = note.isPinned
        editorIsFavorite.value = note.isFavorite
        editorIsLocked.value = note.isLocked
        editorIsChecklist.value = note.isChecklist
        editorDrawingStrokes.value = note.getDrawingStrokes()
        editorAudioPath.value = note.audioPath
        editorAudioDuration.value = note.audioDurationMs
        editorTags.value = note.tags
        editorSnapshots.value = note.getHistorySnapshots()
        editorCreatedAt.value = note.createdAt
        editorUpdatedAt.value = note.updatedAt

        if (note.isChecklist) {
            val items = note.getChecklistItems()
            editorChecklistItems.value = if (items.isEmpty()) listOf(ChecklistItem(text = "", isChecked = false)) else items
        } else {
            editorChecklistItems.value = emptyList()
        }

        navigateTo(Screen.Editor(noteId = note.id, isChecklist = note.isChecklist))
    }

    fun saveCurrentNote() {
        val title = editorTitle.value.trim()
        val content = editorContent.value.trim()
        val isChecklist = editorIsChecklist.value
        val items = editorChecklistItems.value.filter { it.text.isNotBlank() }
        val hasDrawing = editorDrawingStrokes.value.isNotEmpty()
        val hasAudio = editorAudioPath.value.isNotBlank()

        if (title.isEmpty() && content.isEmpty() && (!isChecklist || items.isEmpty()) && !hasDrawing && !hasAudio) {
            return
        }

        viewModelScope.launch {
            val checklistJson = if (isChecklist) ChecklistItem.listToJson(items) else ""
            val drawingData = DrawingStroke.listToJson(editorDrawingStrokes.value)
            val noteId = _editingNoteId.value
            val now = System.currentTimeMillis()

            // Manage snapshots (keep up to 10 latest snapshots)
            val currentSnapshots = editorSnapshots.value.toMutableList()
            if (content.isNotBlank() || title.isNotBlank() || isChecklist) {
                val shouldAddSnapshot = currentSnapshots.isEmpty() ||
                        (now - currentSnapshots.last().timestamp > 60_000) // Snapshot every minute of edits
                if (shouldAddSnapshot) {
                    currentSnapshots.add(
                        NoteSnapshot(
                            timestamp = now,
                            title = title,
                            content = content,
                            checklistJson = checklistJson
                        )
                    )
                    if (currentSnapshots.size > 10) currentSnapshots.removeAt(0)
                    editorSnapshots.value = currentSnapshots
                }
            }

            val note = Note(
                id = noteId ?: 0L,
                title = title,
                content = content,
                category = editorCategory.value,
                colorHex = editorColorHex.value,
                isPinned = editorIsPinned.value,
                isFavorite = editorIsFavorite.value,
                isLocked = editorIsLocked.value,
                isChecklist = isChecklist,
                checklistJson = checklistJson,
                drawingData = drawingData,
                audioPath = editorAudioPath.value,
                audioDurationMs = editorAudioDuration.value,
                tags = editorTags.value,
                historyJson = NoteSnapshot.listToJson(currentSnapshots),
                createdAt = editorCreatedAt.value,
                updatedAt = now
            )

            if (noteId == null || noteId == 0L) {
                val newId = repository.insertNote(note)
                _editingNoteId.value = newId
            } else {
                repository.updateNote(note)
            }

            // Auto-backup to disk
            val context = getApplication<Application>()
            BackupManager.saveAutoBackup(context, rawActiveNotes.value)
        }
    }

    fun applyFormatting(prefix: String, suffix: String) {
        val current = editorContent.value
        editorContent.value = current + prefix + suffix
        saveCurrentNote()
    }

    fun saveDrawing(strokes: List<DrawingStroke>) {
        editorDrawingStrokes.value = strokes
        saveCurrentNote()
    }

    fun saveAudio(path: String, durationMs: Long) {
        editorAudioPath.value = path
        editorAudioDuration.value = durationMs
        saveCurrentNote()
    }

    fun deleteAudio() {
        editorAudioPath.value = ""
        editorAudioDuration.value = 0L
        saveCurrentNote()
    }

    fun restoreSnapshot(snapshot: NoteSnapshot) {
        editorTitle.value = snapshot.title
        editorContent.value = snapshot.content
        if (snapshot.checklistJson.isNotBlank()) {
            editorChecklistItems.value = ChecklistItem.listFromJson(snapshot.checklistJson)
            editorIsChecklist.value = true
        }
        saveCurrentNote()
    }

    fun setMasterPin(pin: String) {
        _appMasterPin.value = pin
        prefs.edit().putString("master_pin", pin).apply()
    }

    fun unlockNoteSession(noteId: Long) {
        _unlockedNotesSet.value = _unlockedNotesSet.value + noteId
    }

    fun toggleChecklistMode() {
        val current = editorIsChecklist.value
        if (!current) {
            val lines = editorContent.value.lines().filter { it.isNotBlank() }
            val newItems = if (lines.isNotEmpty()) {
                lines.map { ChecklistItem(text = it, isChecked = false) }
            } else {
                listOf(ChecklistItem(text = "", isChecked = false))
            }
            editorChecklistItems.value = newItems
            editorIsChecklist.value = true
        } else {
            val text = editorChecklistItems.value
                .filter { it.text.isNotBlank() }
                .joinToString("\n") { if (it.isChecked) "[✓] ${it.text}" else "[ ] ${it.text}" }
            editorContent.value = text
            editorIsChecklist.value = false
        }
        saveCurrentNote()
    }

    fun addChecklistItem(afterIndex: Int? = null) {
        val current = editorChecklistItems.value.toMutableList()
        val newItem = ChecklistItem(text = "", isChecked = false)
        if (afterIndex != null && afterIndex in 0..current.size) {
            current.add(afterIndex + 1, newItem)
        } else {
            current.add(newItem)
        }
        editorChecklistItems.value = current
    }

    fun updateChecklistItemText(index: Int, text: String) {
        if (index in editorChecklistItems.value.indices) {
            val current = editorChecklistItems.value.toMutableList()
            current[index] = current[index].copy(text = text)
            editorChecklistItems.value = current
        }
    }

    fun toggleChecklistItem(index: Int) {
        if (index in editorChecklistItems.value.indices) {
            val current = editorChecklistItems.value.toMutableList()
            current[index] = current[index].copy(isChecked = !current[index].isChecked)
            editorChecklistItems.value = current
            saveCurrentNote()
        }
    }

    fun removeChecklistItem(index: Int) {
        if (index in editorChecklistItems.value.indices) {
            val current = editorChecklistItems.value.toMutableList()
            current.removeAt(index)
            if (current.isEmpty()) {
                current.add(ChecklistItem(text = "", isChecked = false))
            }
            editorChecklistItems.value = current
            saveCurrentNote()
        }
    }

    fun quickToggleChecklistOnCard(note: Note, itemIndex: Int) {
        viewModelScope.launch {
            val items = note.getChecklistItems().toMutableList()
            if (itemIndex in items.indices) {
                items[itemIndex] = items[itemIndex].copy(isChecked = !items[itemIndex].isChecked)
                val updatedNote = note.copy(
                    checklistJson = ChecklistItem.listToJson(items),
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateNote(updatedNote)
            }
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch { repository.togglePin(note) }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch { repository.toggleFavorite(note) }
    }

    fun toggleArchive(note: Note) {
        viewModelScope.launch { repository.toggleArchive(note) }
    }

    fun toggleNoteLock(note: Note) {
        viewModelScope.launch {
            val updated = note.copy(isLocked = !note.isLocked, updatedAt = System.currentTimeMillis())
            repository.updateNote(updated)
        }
    }

    fun moveToTrash(note: Note) {
        viewModelScope.launch { repository.moveToTrash(note) }
    }

    fun restoreFromTrash(note: Note) {
        viewModelScope.launch { repository.restoreFromTrash(note) }
    }

    fun deletePermanently(note: Note) {
        viewModelScope.launch { repository.deletePermanently(note.id) }
    }

    fun emptyTrash() {
        viewModelScope.launch { repository.emptyTrash() }
    }

    fun restoreAllTrash() {
        viewModelScope.launch { repository.restoreAllTrash() }
    }

    fun duplicateNote(note: Note) {
        viewModelScope.launch {
            val copy = note.copy(
                id = 0L,
                title = "${note.title} (نسخة)",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.insertNote(copy)
        }
    }

    fun addCustomCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !_customCategories.value.contains(trimmed)) {
            _customCategories.value = _customCategories.value + trimmed
        }
    }

    fun toggleGridView() {
        isGridView.value = !isGridView.value
    }

    fun importNotesList(notes: List<Note>) {
        viewModelScope.launch {
            for (note in notes) {
                repository.insertNote(note.copy(id = 0L))
            }
        }
    }
}
