package com.aitia.app.domain.model

import java.time.Instant

data class EnvironmentProfile(
    val id: Long = 0,
    val projectId: Long? = null,
    val name: String,
    val device: String = "Pixel 8",
    val androidVersion: String = "Android 15",
    val appVersion: String = "1.0.0",
    val buildNumber: String = "",
    val notes: String = "",
    val createdAt: Instant = Instant.now()
)
