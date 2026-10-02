package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class NoteCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

object DefaultCategories {
    val ALL = "الكل"
    val GENERAL = "عام"
    val PERSONAL = "شخصي"
    val WORK = "عمل"
    val STUDY = "دراسة"
    val IDEAS = "أفكار"
    val TASKS = "مهام"
    val IMPORTANT = "هام"

    val list = listOf(
        NoteCategory(GENERAL, Icons.Default.Folder, Color(0xFF64748B)),
        NoteCategory(PERSONAL, Icons.Default.Person, Color(0xFF3B82F6)),
        NoteCategory(WORK, Icons.Default.Work, Color(0xFF8B5CF6)),
        NoteCategory(STUDY, Icons.Default.School, Color(0xFF10B981)),
        NoteCategory(IDEAS, Icons.Default.Lightbulb, Color(0xFFF59E0B)),
        NoteCategory(TASKS, Icons.Default.CheckCircle, Color(0xFFEC4899)),
        NoteCategory(IMPORTANT, Icons.Default.Star, Color(0xFFEF4444)),
    )

    fun getCategoryColor(name: String): Color {
        return list.find { it.name == name }?.color ?: Color(0xFF6366F1)
    }

    fun getCategoryIcon(name: String): ImageVector {
        return list.find { it.name == name }?.icon ?: Icons.Default.Bookmark
    }
}

data class NoteColorOption(
    val hex: String,
    val lightColor: Color,
    val darkColor: Color,
    val name: String
)

object NoteColorPalette {
    val options = listOf(
        NoteColorOption("#00000000", Color.Transparent, Color.Transparent, "افتراضي"),
        NoteColorOption("#FEE2E2", Color(0xFFFEE2E2), Color(0xFF451A1A), "وردي ناعم"),
        NoteColorOption("#FEF3C7", Color(0xFFFEF3C7), Color(0xFF452D12), "أصفر دافئ"),
        NoteColorOption("#D1FAE5", Color(0xFFD1FAE5), Color(0xFF133E2C), "نعناعي هادئ"),
        NoteColorOption("#E0E7FF", Color(0xFFE0E7FF), Color(0xFF1E265A), "أزرق نيلي"),
        NoteColorOption("#F3E8FF", Color(0xFFF3E8FF), Color(0xFF3B1E54), "بنفسجي فاتح"),
        NoteColorOption("#CFFAFE", Color(0xFFCFFAFE), Color(0xFF15444D), "سماوي"),
        NoteColorOption("#FFEDD5", Color(0xFFFFEDD5), Color(0xFF4D2810), "برتقالي هادئ"),
        NoteColorOption("#FCE7F3", Color(0xFFFCE7F3), Color(0xFF4D1734), "فوشيا ناعم"),
        NoteColorOption("#F1F5F9", Color(0xFFF1F5F9), Color(0xFF242C38), "رمادي حديث")
    )
}
