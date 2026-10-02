package com.example.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private val timeFormat = SimpleDateFormat("h:mm a", Locale("ar"))
    private val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
    private val shortDateFormat = SimpleDateFormat("d MMM", Locale("ar"))

    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            diff < 60_000 -> "الآن"
            minutes < 60 -> "منذ $minutes دقيقة"
            hours < 24 -> "اليوم ${timeFormat.format(Date(timestamp))}"
            days == 1L -> "أمس ${timeFormat.format(Date(timestamp))}"
            days < 7 -> "منذ $days أيام"
            else -> shortDateFormat.format(Date(timestamp))
        }
    }

    fun formatFullDateTime(timestamp: Long): String {
        val dateStr = dateFormat.format(Date(timestamp))
        val timeStr = timeFormat.format(Date(timestamp))
        return "$dateStr • $timeStr"
    }
}
