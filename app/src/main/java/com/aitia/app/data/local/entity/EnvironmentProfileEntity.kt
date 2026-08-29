package com.aitia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "environment_profiles")
data class EnvironmentProfileEntity(
    @PrimaryKey(autoGenerate = true)
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

@Entity(tableName = "testing_sessions")
data class TestingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long? = null,
    val name: String,
    val startedAt: Instant = Instant.now(),
    val endedAt: Instant? = null,
    val environmentId: Long? = null,
    val notes: String = ""
)
