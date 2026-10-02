package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.model.ChecklistItem
import com.example.data.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NoteRepository(private val noteDao: NoteDao) {

    val activeNotes: Flow<List<Note>> = noteDao.getAllActiveNotes()
    val archivedNotes: Flow<List<Note>> = noteDao.getArchivedNotes()
    val trashedNotes: Flow<List<Note>> = noteDao.getTrashedNotes()
    val favoriteNotes: Flow<List<Note>> = noteDao.getFavoriteNotes()
    val activeCount: Flow<Int> = noteDao.getActiveNotesCount()

    fun getNoteById(id: Long): Flow<Note?> = noteDao.getNoteById(id)

    suspend fun getNoteByIdOnce(id: Long): Note? = noteDao.getNoteByIdOnce(id)

    suspend fun insertNote(note: Note): Long {
        val now = System.currentTimeMillis()
        val preparedNote = note.copy(
            createdAt = if (note.createdAt == 0L) now else note.createdAt,
            updatedAt = now
        )
        return noteDao.insertNote(preparedNote)
    }

    suspend fun updateNote(note: Note) {
        val updated = note.copy(updatedAt = System.currentTimeMillis())
        noteDao.updateNote(updated)
    }

    suspend fun moveToTrash(note: Note) {
        noteDao.updateNote(note.copy(isTrashed = true, isPinned = false, updatedAt = System.currentTimeMillis()))
    }

    suspend fun restoreFromTrash(note: Note) {
        noteDao.updateNote(note.copy(isTrashed = false, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deletePermanently(id: Long) {
        noteDao.deletePermanently(id)
    }

    suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    suspend fun restoreAllTrash() {
        noteDao.restoreAllTrash()
    }

    suspend fun togglePin(note: Note) {
        noteDao.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleFavorite(note: Note) {
        noteDao.updateNote(note.copy(isFavorite = !note.isFavorite, updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleArchive(note: Note) {
        noteDao.updateNote(
            note.copy(
                isArchived = !note.isArchived,
                isPinned = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun seedInitialNotesIfEmpty() {
        val count = noteDao.getActiveNotesCount().first()
        if (count == 0) {
            val now = System.currentTimeMillis()

            val welcomeNote = Note(
                title = "مرحباً بك في تطبيق ملاحظاتي 🌟",
                content = """أهلاً بك في تطبيقك الاحترافي الجديد لتدوين الملاحظات وإدارة الأفكار والمهام اليومية!

✨ الميزات الرئيسية:
• إنشاء ملاحظات نصية سريعة ومنسقة
• قوائم مهام تفاعلية (To-Do Lists) قابلة للتعليم
• تنظيم وتصنيف الملاحظات (شخصي، عمل، دراسة، أفكار، هام)
• ألوان متعددة وجذابة لكل ملاحظة لتنظيم بصري مريح
• تثبيت الملاحظات الهامة في أعلى الشاشة 📌
• بحث فوري وسريع في العناوين والمحتوى
• سلة محذوفات وأرشيف للحفاظ على تنظيم دفترك
• إحصائيات دقيقة لعدد الكلمات ومعدل إنجاز المهام

ابدأ الآن بتعديل هذه الملاحظة أو اضغط على زر (+) لإنشاء ملاحظتك الأولى!""",
                category = "عام",
                colorHex = "#E0E7FF",
                isPinned = true,
                isFavorite = true,
                createdAt = now - 100000,
                updatedAt = now - 100000
            )

            val checklistTasks = listOf(
                ChecklistItem(text = "استكشاف ميزات تطبيق الملاحظات", isChecked = true),
                ChecklistItem(text = "إنشاء أول قائمة مهام للأسبوع", isChecked = false),
                ChecklistItem(text = "تجربة تغيير لون وتصنيف الملاحظة", isChecked = false),
                ChecklistItem(text = "تثبيت ملاحظة هامة في الواجهة الرئيسية", isChecked = false)
            )

            val sampleChecklist = Note(
                title = "أهداف اليوم والمهام القادمة 🚀",
                content = "",
                category = "مهام",
                colorHex = "#D1FAE5",
                isPinned = true,
                isChecklist = true,
                checklistJson = ChecklistItem.listToJson(checklistTasks),
                createdAt = now - 50000,
                updatedAt = now - 50000
            )

            val ideasNote = Note(
                title = "أفكار لمشاريع وتطوير الذات 💡",
                content = """- قراءة 20 صفحة يومياً من كتاب مفيد
- تعلم مهارة برمجية جديدة
- ممارسة الرياضة الصباحية 3 مرات أسبوعياً
- تنظيم مساحة العمل وتدوين الملاحظات اليومية باهتمام""",
                category = "أفكار",
                colorHex = "#FEF3C7",
                isPinned = false,
                isFavorite = true,
                createdAt = now - 20000,
                updatedAt = now - 20000
            )

            noteDao.insertNote(welcomeNote)
            noteDao.insertNote(sampleChecklist)
            noteDao.insertNote(ideasNote)
        }
    }
}
