package com.aitia.app.domain.model

import java.time.Instant

data class Project(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val packageName: String = "",
    val platform: String = "Android",
    val currentVersion: String = "1.0.0",
    val colorHex: String = "#58A6FF",
    val iconName: String = "code",
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val openIssueCount: Int = 0,
    val criticalIssueCount: Int = 0,
    val totalIssueCount: Int = 0
)
