package com.aitia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val packageName: String = "",
    val platform: String = "Android",
    val currentVersion: String = "1.0.0",
    val colorHex: String = "#58A6FF",
    val iconName: String = "code",
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

@Entity(
    tableName = "project_versions"
)
data class ProjectVersionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val versionName: String,
    val buildNumber: String = "",
    val releaseDate: Instant? = null,
    val isCurrent: Boolean = false,
    val createdAt: Instant = Instant.now()
)
