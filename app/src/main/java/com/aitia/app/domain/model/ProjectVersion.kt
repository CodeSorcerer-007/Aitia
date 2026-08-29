package com.aitia.app.domain.model

import java.time.Instant

data class ProjectVersion(
    val id: Long = 0,
    val projectId: Long,
    val versionName: String,
    val buildNumber: String = "",
    val releaseDate: Instant? = null,
    val isCurrent: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val issueCount: Int = 0,
    val resolvedCount: Int = 0
)
