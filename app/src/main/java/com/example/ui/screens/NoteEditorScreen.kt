package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultCategories
import com.example.data.model.NoteColorPalette
import com.example.ui.components.ChecklistEditor
import com.example.ui.components.ColorPickerRow
import com.example.ui.components.DrawingCanvasDialog
import com.example.ui.components.DrawingPreview
import com.example.ui.components.HistorySnapshotDialog
import com.example.ui.components.RichFormattingToolbar
import com.example.ui.components.VoiceMemoWidget
import com.example.ui.util.DateTimeUtils
import com.example.ui.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val title by viewModel.editorTitle.collectAsState()
    val content by viewModel.editorContent.collectAsState()
    val category by viewModel.editorCategory.collectAsState()
    val colorHex by viewModel.editorColorHex.collectAsState()
    val isPinned by viewModel.editorIsPinned.collectAsState()
    val isFavorite by viewModel.editorIsFavorite.collectAsState()
    val isLocked by viewModel.editorIsLocked.collectAsState()
    val isChecklist by viewModel.editorIsChecklist.collectAsState()
    val checklistItems by viewModel.editorChecklistItems.collectAsState()
    val drawingStrokes by viewModel.editorDrawingStrokes.collectAsState()
    val audioPath by viewModel.editorAudioPath.collectAsState()
    val tags by viewModel.editorTags.collectAsState()
    val snapshots by viewModel.editorSnapshots.collectAsState()
    val updatedAt by viewModel.editorUpdatedAt.collectAsState()
    val customCategories by viewModel.customCategories.collectAsState()

    var showCategoryMenu by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showDrawingDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showVoiceRecorder by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateBack()
    }

    if (showDrawingDialog) {
        DrawingCanvasDialog(
            initialStrokes = drawingStrokes,
            onSave = { newStrokes ->
                viewModel.saveDrawing(newStrokes)
                showDrawingDialog = false
            },
            onDismiss = { showDrawingDialog = false }
        )
    }

    if (showHistoryDialog) {
        HistorySnapshotDialog(
            snapshots = snapshots,
            onRestoreSnapshot = { snapshot ->
                viewModel.restoreSnapshot(snapshot)
                showHistoryDialog = false
            },
            onDismiss = { showHistoryDialog = false }
        )
    }

    val colorOption = NoteColorPalette.options.find { it.hex == colorHex }
    val bgColor = if (colorOption != null && colorOption.hex != "#00000000") {
        if (isDark) colorOption.darkColor else colorOption.lightColor
    } else {
        MaterialTheme.colorScheme.background
    }

    val animatedBg by animateColorAsState(targetValue = bgColor, label = "editorBg")
    val categoryColor = DefaultCategories.getCategoryColor(category)
    val categoryIcon = DefaultCategories.getCategoryIcon(category)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBg)
            .imePadding(),
        containerColor = animatedBg,
        topBar = {
            TopAppBar(
                title = {
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(categoryColor.copy(alpha = 0.15f))
                                .border(1.dp, categoryColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .clickable { showCategoryMenu = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                color = categoryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            customCategories.forEach { catName ->
                                val itemColor = DefaultCategories.getCategoryColor(catName)
                                val itemIcon = DefaultCategories.getCategoryIcon(catName)
                                DropdownMenuItem(
                                    text = { Text(catName, fontWeight = if (catName == category) FontWeight.Bold else FontWeight.Normal) },
                                    leadingIcon = { Icon(itemIcon, contentDescription = null, tint = itemColor) },
                                    onClick = {
                                        viewModel.editorCategory.value = catName
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع وحفظ"
                        )
                    }
                },
                actions = {
                    // Pin toggle
                    IconButton(
                        onClick = { viewModel.editorIsPinned.value = !viewModel.editorIsPinned.value },
                        modifier = Modifier.testTag("editor_pin_toggle")
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (isPinned) "إلغاء التثبيت" else "تثبيت",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Favorite toggle
                    IconButton(
                        onClick = { viewModel.editorIsFavorite.value = !viewModel.editorIsFavorite.value }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (isFavorite) "في المفضلة" else "إضافة للمفضلة",
                            tint = if (isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Lock toggle
                    IconButton(
                        onClick = { viewModel.editorIsLocked.value = !viewModel.editorIsLocked.value }
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (isLocked) "ملاحظة محمية" else "قفل الملاحظة",
                            tint = if (isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Drawing button
                    IconButton(
                        onClick = { showDrawingDialog = true },
                        modifier = Modifier.testTag("editor_drawing_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = "رسم يدوي",
                            tint = if (drawingStrokes.isNotEmpty()) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Voice Memo button
                    IconButton(
                        onClick = { showVoiceRecorder = !showVoiceRecorder },
                        modifier = Modifier.testTag("editor_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "تسجيل صوتي",
                            tint = if (audioPath.isNotBlank() || showVoiceRecorder) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Color picker toggle
                    IconButton(
                        onClick = { showColorPicker = !showColorPicker },
                        modifier = Modifier.testTag("editor_color_palette_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "اختيار لون الملاحظة",
                            tint = if (showColorPicker) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Checklist toggle
                    IconButton(
                        onClick = { viewModel.toggleChecklistMode() },
                        modifier = Modifier.testTag("toggle_checklist_mode_button")
                    ) {
                        Icon(
                            imageVector = if (isChecklist) Icons.Default.Description else Icons.Default.Checklist,
                            contentDescription = if (isChecklist) "تحويل لنص عادي" else "تحويل لقائمة مهام",
                            tint = if (isChecklist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // More actions menu
                    Box {
                        IconButton(onClick = { showOptionsMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "المزيد من الخيارات"
                            )
                        }

                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("سجل التعديلات (Snapshots)") },
                                leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                                onClick = {
                                    showOptionsMenu = false
                                    showHistoryDialog = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("مشاركة الملاحظة") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    showOptionsMenu = false
                                    val shareText = buildString {
                                        if (title.isNotBlank()) appendLine(title)
                                        if (isChecklist) {
                                            checklistItems.forEach { item ->
                                                appendLine(if (item.isChecked) "[✓] ${item.text}" else "[ ] ${item.text}")
                                            }
                                        } else {
                                            appendLine(content)
                                        }
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, title)
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "مشاركة عبر"))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("حفظ الملاحظة") },
                                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                                onClick = {
                                    showOptionsMenu = false
                                    viewModel.saveCurrentNote()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("نقل إلى سلة المحذوفات", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showOptionsMenu = false
                                    viewModel.saveCurrentNote()
                                    viewModel.navigateBack()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            if (!isChecklist) {
                RichFormattingToolbar(
                    onApplyFormat = { prefix, suffix ->
                        viewModel.applyFormatting(prefix, suffix)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Color Palette Selector
            if (showColorPicker) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    ColorPickerRow(
                        selectedHex = colorHex,
                        onColorSelected = { viewModel.editorColorHex.value = it }
                    )
                }
            }

            // Voice Memo Recording & Playback Widget
            if (showVoiceRecorder || audioPath.isNotBlank()) {
                VoiceMemoWidget(
                    audioPath = audioPath,
                    onAudioRecorded = { path, duration ->
                        viewModel.saveAudio(path, duration)
                    },
                    onDeleteAudio = {
                        viewModel.deleteAudio()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Embedded Drawing Preview Card
            if (drawingStrokes.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "رسم يدوي مرفق 🎨",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row {
                                IconButton(onClick = { showDrawingDialog = true }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Brush, contentDescription = "تعديل الرسمة", modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { viewModel.saveDrawing(emptyList()) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف الرسمة", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        DrawingPreview(strokes = drawingStrokes, height = 140.dp)
                    }
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    viewModel.editorTitle.value = it
                    viewModel.saveCurrentNote()
                },
                placeholder = {
                    Text(
                        text = if (isChecklist) "عنوان قائمة المهام..." else "عنوان الملاحظة...",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    )
                },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("note_title_input")
            )

            // Tags Field
            OutlinedTextField(
                value = tags,
                onValueChange = {
                    viewModel.editorTags.value = it
                    viewModel.saveCurrentNote()
                },
                placeholder = { Text("وسوم وكلمات مفتاحية (مثال: أفكار, مشروع, 2026)...", fontSize = 12.sp) },
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Content Area: Checklist or Text Field
            if (isChecklist) {
                ChecklistEditor(
                    items = checklistItems,
                    onToggleItem = { index -> viewModel.toggleChecklistItem(index) },
                    onUpdateText = { index, text -> viewModel.updateChecklistItemText(index, text) },
                    onRemoveItem = { index -> viewModel.removeChecklistItem(index) },
                    onAddItem = { viewModel.addChecklistItem() },
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        viewModel.editorContent.value = it
                        viewModel.saveCurrentNote()
                    },
                    placeholder = {
                        Text(
                            text = "اكتب ملاحظتك وأفكارك هنا بكل حرية...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("note_content_input")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer statistics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val words = if (isChecklist) checklistItems.joinToString(" ") { it.text } else "$title $content"
                val wordCount = words.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                val charCount = if (isChecklist) checklistItems.sumOf { it.text.length } else content.length

                Text(
                    text = "$wordCount كلمة • $charCount حرف",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Text(
                    text = "آخر حفظ: ${DateTimeUtils.formatRelativeTime(updatedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
