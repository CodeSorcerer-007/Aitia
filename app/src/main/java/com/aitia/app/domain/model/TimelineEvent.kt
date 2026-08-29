package com.aitia.app.domain.model

import java.time.Instant

data class TimelineEvent(
    val id: Long = 0,
    val issueId: Long,
    val eventType: String,
    val title: String,
    val description: String = "",
    val timestamp: Instant = Instant.now()
)
