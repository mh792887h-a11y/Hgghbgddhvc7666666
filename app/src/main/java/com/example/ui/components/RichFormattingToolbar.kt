package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FormatAction(
    val id: String,
    val label: String,
    val icon: ImageVector? = null,
    val prefix: String = "",
    val suffix: String = ""
)

@Composable
fun RichFormattingToolbar(
    onApplyFormat: (prefix: String, suffix: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        FormatAction("h1", "H1", prefix = "\n# "),
        FormatAction("h2", "H2", prefix = "\n## "),
        FormatAction("h3", "H3", prefix = "\n### "),
        FormatAction("bold", "غامق", icon = Icons.Default.FormatBold, prefix = "**", suffix = "**"),
        FormatAction("italic", "مائل", icon = Icons.Default.FormatItalic, prefix = "*", suffix = "*"),
        FormatAction("strike", "مشطوب", icon = Icons.Default.FormatStrikethrough, prefix = "~~", suffix = "~~"),
        FormatAction("bullet", "نقطة", icon = Icons.AutoMirrored.Filled.FormatListBulleted, prefix = "\n• "),
        FormatAction("number", "رقمي", icon = Icons.Default.FormatListNumbered, prefix = "\n1. "),
        FormatAction("quote", "اقتباس", icon = Icons.Default.FormatQuote, prefix = "\n> "),
        FormatAction("code", "كود", icon = Icons.Default.Code, prefix = "`", suffix = "`"),
        FormatAction("divider", "فاصل", icon = Icons.Default.HorizontalRule, prefix = "\n---\n"),
        FormatAction("time", "توقيت", icon = Icons.Default.Schedule, prefix = SimpleDateFormat(" [d MMMM yyyy - h:mm a] ", Locale("ar")).format(Date()))
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(actions) { action ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onApplyFormat(action.prefix, action.suffix) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (action.icon != null) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.label,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = action.label,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
