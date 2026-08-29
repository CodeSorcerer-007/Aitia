package com.aitia.app.domain.model

import java.time.Duration
import java.time.Instant

data class TestingSession(
    val id: Long = 0,
    val projectId: Long? = null,
    val projectName: String? = null,
    val name: String,
    val startedAt: Instant = Instant.now(),
    val endedAt: Instant? = null,
    val environmentId: Long? = null,
    val environmentName: String? = null,
    val notes: String = "",
    val totalIssuesFound: Int = 0,
    val crashesFound: Int = 0,
    val bugsFound: Int = 0
) {
    val isActive: Boolean
        get() = endedAt == null

    val duration: Duration
        get() = Duration.between(startedAt, endedAt ?: Instant.now())

    val formattedDuration: String
        get() {
            val d = duration
            val hours = d.toHours()
            val minutes = d.toMinutes() % 60
            val seconds = d.seconds % 60
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }
}
