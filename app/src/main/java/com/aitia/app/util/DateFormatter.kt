package com.aitia.app.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    private val ABSOLUTE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy · HH:mm", Locale.getDefault())
    private val TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

    fun formatRelativeTime(instant: Instant?): String {
        if (instant == null) return ""
        val now = Instant.now()
        val duration = Duration.between(instant, now)

        val seconds = duration.seconds
        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> formatAbsoluteDate(instant)
        }
    }

    fun formatAbsolute(instant: Instant?): String {
        if (instant == null) return ""
        return instant.atZone(ZoneId.systemDefault()).format(ABSOLUTE_FORMATTER)
    }

    fun formatTimeOnly(instant: Instant?): String {
        if (instant == null) return ""
        return instant.atZone(ZoneId.systemDefault()).format(TIME_ONLY_FORMATTER)
    }

    fun formatAbsoluteDate(instant: Instant?): String {
        if (instant == null) return ""
        return instant.atZone(ZoneId.systemDefault()).format(DATE_ONLY_FORMATTER)
    }
}
