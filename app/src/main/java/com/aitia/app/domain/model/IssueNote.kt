package com.aitia.app.domain.model

import java.time.Instant

data class IssueNote(
    val id: Long = 0,
    val issueId: Long,
    val text: String,
    val createdAt: Instant = Instant.now()
)
