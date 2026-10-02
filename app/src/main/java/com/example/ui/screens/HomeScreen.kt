package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultCategories
import com.example.data.model.Note
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.NoteCard
import com.example.ui.components.PinEntryDialog
import com.example.ui.components.TemplatesDialog
import com.example.ui.viewmodel.NoteFilterType
import com.example.ui.viewmodel.NotesViewModel
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val filteredNotes by viewModel.filteredActiveNotes.collectAsState()
    val allActiveNotes by viewModel.rawActiveNotes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val customCategories by viewModel.customCategories.collectAsState()
    val appMasterPin by viewModel.appMasterPin.collectAsState()
    val unlockedNotes by viewModel.unlockedNotesSet.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var isFabExpanded by remember { mutableStateOf(false) }
    var showTemplatesDialog by remember { mutableStateOf(false) }
    var noteToUnlock by remember { mutableStateOf<Note?>(null) }

    if (showTemplatesDialog) {
        TemplatesDialog(
            onSelectTemplate = { template ->
                showTemplatesDialog = false
                viewModel.openTemplate(template)
            },
            onDismiss = { showTemplatesDialog = false }
        )
    }

    val pinnedNotes = remember(filteredNotes) { filteredNotes.filter { it.isPinned } }
    val unpinnedNotes = remember(filteredNotes) { filteredNotes.filter { !it.isPinned } }

    if (noteToUnlock != null) {
        val targetNote = noteToUnlock!!
        PinEntryDialog(
            title = "أدخل رمز PIN لفتح الملاحظة",
            expectedPin = targetNote.lockPin.ifEmpty { appMasterPin },
            onSuccess = {
                viewModel.unlockNoteSession(targetNote.id)
                viewModel.openEditNote(targetNote)
                noteToUnlock = null
            },
            onDismiss = { noteToUnlock = null }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ملاحظاتي الاحترافية",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${allActiveNotes.size} ملاحظة • حفظ محلي آمن",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Toggle Grid / List
                        IconButton(
                            onClick = { viewModel.toggleGridView() },
                            modifier = Modifier.testTag("toggle_view_mode_button")
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                                contentDescription = if (isGridView) "عرض قائمة" else "عرض شبكي"
                            )
                        }

                        // Sort Menu
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "ترتيب الملاحظات"
                                )
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                SortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option.label,
                                                fontWeight = if (sortOption == option) FontWeight.Bold else FontWeight.Normal,
                                                color = if (sortOption == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            viewModel.sortOption.value = option
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Main Navigation Menu
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "القائمة الرئيسية"
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("الأمان والنسخ الاحتياطي") },
                                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.navigateTo(Screen.BackupSecurity)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("الأرشيف") },
                                    leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.navigateTo(Screen.Archive)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("سلة المحذوفات") },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.navigateTo(Screen.Trash)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("الإحصائيات والملخص") },
                                    leadingIcon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.navigateTo(Screen.Stats)
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Search Bar
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = { Text("ابحث في النصوص، الوسوم #، والقوائم...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "مسح البحث"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("search_notes_input")
                    )
                }

                // Type Filter Chips Row (All, Checklists, Voice, Drawing, Locked, Favorites)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(NoteFilterType.values()) { type ->
                        val isSelected = filterType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .clickable { viewModel.filterType.value = type }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = type.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Category Chips Row
                CategoryChipRow(
                    categories = customCategories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { viewModel.selectedCategory.value = it },
                    onAddCategory = { viewModel.addCustomCategory(it) },
                    allNotes = allActiveNotes,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary FAB: Templates
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "قوالب جاهزة",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        SmallFloatingActionButton(
                            onClick = {
                                isFabExpanded = false
                                showTemplatesDialog = true
                            },
                            containerColor = Color(0xFFFEF3C7),
                            contentColor = Color(0xFF92400E),
                            modifier = Modifier.testTag("templates_fab")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "قوالب جاهزة")
                        }
                    }
                }

                // Secondary FAB: Checklist
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "قائمة مهام جديدة",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        SmallFloatingActionButton(
                            onClick = {
                                isFabExpanded = false
                                viewModel.openNewNote(isChecklist = true)
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.testTag("new_checklist_fab")
                        ) {
                            Icon(Icons.Default.Checklist, contentDescription = "قائمة مهام جديدة")
                        }
                    }
                }

                // Secondary FAB: Text Note
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ملاحظة نصية",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        SmallFloatingActionButton(
                            onClick = {
                                isFabExpanded = false
                                viewModel.openNewNote(isChecklist = false)
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("new_text_note_fab")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "ملاحظة جديدة")
                        }
                    }
                }

                // Main Toggle FAB
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("main_add_fab")
                ) {
                    Icon(
                        imageVector = if (isFabExpanded) Icons.Default.Clear else Icons.Default.Add,
                        contentDescription = "إضافة ملاحظة أو قائمة"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (filteredNotes.isEmpty()) {
                if (searchQuery.isNotBlank() || selectedCategory != DefaultCategories.ALL || filterType != NoteFilterType.ALL) {
                    EmptyStateView(
                        title = "لا توجد نتائج مطابقة",
                        subtitle = "جرب البحث بكلمات أخرى أو تغيير الفلاتر والتصنيفات",
                        actionButtonText = "عرض كل الملاحظات",
                        onActionClick = {
                            viewModel.searchQuery.value = ""
                            viewModel.selectedCategory.value = DefaultCategories.ALL
                            viewModel.filterType.value = NoteFilterType.ALL
                        }
                    )
                } else {
                    EmptyStateView(
                        title = "دفتر ملاحظاتك آمن وفارغ الآن",
                        subtitle = "اضغط على زر (+) لتدوين فكرة سريعة أو قائمة مهام تفاعلية",
                        actionButtonText = "إنشاء أول ملاحظة",
                        onActionClick = { viewModel.openNewNote(isChecklist = false) }
                    )
                }
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (pinnedNotes.isNotEmpty()) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                                SectionHeader(title = "الملاحظات المثبتة", icon = Icons.Default.PushPin)
                            }
                            items(pinnedNotes, key = { it.id }) { note ->
                                val isUnlocked = unlockedNotes.contains(note.id)
                                NoteCard(
                                    note = note,
                                    isUnlocked = isUnlocked,
                                    onClick = {
                                        if (note.isLocked && !isUnlocked) {
                                            noteToUnlock = note
                                        } else {
                                            viewModel.openEditNote(note)
                                        }
                                    },
                                    onTogglePin = { viewModel.togglePin(note) },
                                    onToggleFavorite = { viewModel.toggleFavorite(note) },
                                    onToggleArchive = { viewModel.toggleArchive(note) },
                                    onToggleLock = { viewModel.toggleNoteLock(note) },
                                    onMoveToTrash = { viewModel.moveToTrash(note) },
                                    onDuplicate = { viewModel.duplicateNote(note) },
                                    onToggleChecklistItem = { idx -> viewModel.quickToggleChecklistOnCard(note, idx) }
                                )
                            }
                        }

                        if (unpinnedNotes.isNotEmpty()) {
                            if (pinnedNotes.isNotEmpty()) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                                    SectionHeader(title = "الملاحظات الأخرى")
                                }
                            }
                            items(unpinnedNotes, key = { it.id }) { note ->
                                val isUnlocked = unlockedNotes.contains(note.id)
                                NoteCard(
                                    note = note,
                                    isUnlocked = isUnlocked,
                                    onClick = {
                                        if (note.isLocked && !isUnlocked) {
                                            noteToUnlock = note
                                        } else {
                                            viewModel.openEditNote(note)
                                        }
                                    },
                                    onTogglePin = { viewModel.togglePin(note) },
                                    onToggleFavorite = { viewModel.toggleFavorite(note) },
                                    onToggleArchive = { viewModel.toggleArchive(note) },
                                    onToggleLock = { viewModel.toggleNoteLock(note) },
                                    onMoveToTrash = { viewModel.moveToTrash(note) },
                                    onDuplicate = { viewModel.duplicateNote(note) },
                                    onToggleChecklistItem = { idx -> viewModel.quickToggleChecklistOnCard(note, idx) }
                                )
                            }
                        }

                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (pinnedNotes.isNotEmpty()) {
                            item {
                                SectionHeader(title = "الملاحظات المثبتة", icon = Icons.Default.PushPin)
                            }
                            items(pinnedNotes, key = { it.id }) { note ->
                                val isUnlocked = unlockedNotes.contains(note.id)
                                NoteCard(
                                    note = note,
                                    isUnlocked = isUnlocked,
                                    onClick = {
                                        if (note.isLocked && !isUnlocked) {
                                            noteToUnlock = note
                                        } else {
                                            viewModel.openEditNote(note)
                                        }
                                    },
                                    onTogglePin = { viewModel.togglePin(note) },
                                    onToggleFavorite = { viewModel.toggleFavorite(note) },
                                    onToggleArchive = { viewModel.toggleArchive(note) },
                                    onToggleLock = { viewModel.toggleNoteLock(note) },
                                    onMoveToTrash = { viewModel.moveToTrash(note) },
                                    onDuplicate = { viewModel.duplicateNote(note) },
                                    onToggleChecklistItem = { idx -> viewModel.quickToggleChecklistOnCard(note, idx) }
                                )
                            }
                        }

                        if (unpinnedNotes.isNotEmpty()) {
                            if (pinnedNotes.isNotEmpty()) {
                                item {
                                    SectionHeader(title = "الملاحظات الأخرى")
                                }
                            }
                            items(unpinnedNotes, key = { it.id }) { note ->
                                val isUnlocked = unlockedNotes.contains(note.id)
                                NoteCard(
                                    note = note,
                                    isUnlocked = isUnlocked,
                                    onClick = {
                                        if (note.isLocked && !isUnlocked) {
                                            noteToUnlock = note
                                        } else {
                                            viewModel.openEditNote(note)
                                        }
                                    },
                                    onTogglePin = { viewModel.togglePin(note) },
                                    onToggleFavorite = { viewModel.toggleFavorite(note) },
                                    onToggleArchive = { viewModel.toggleArchive(note) },
                                    onToggleLock = { viewModel.toggleNoteLock(note) },
                                    onMoveToTrash = { viewModel.moveToTrash(note) },
                                    onDuplicate = { viewModel.duplicateNote(note) },
                                    onToggleChecklistItem = { idx -> viewModel.quickToggleChecklistOnCard(note, idx) }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
