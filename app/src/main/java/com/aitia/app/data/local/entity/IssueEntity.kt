package com.aitia.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import java.time.Instant

@Entity(
    tableName = "issues",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["status"]),
        Index(value = ["priority"]),
        Index(value = ["type"]),
        Index(value = ["testingSessionId"]),
        Index(value = ["isPinned"]),
        Index(value = ["isArchived"]),
        Index(value = ["createdAt"])
    ]
)
data class IssueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long? = null,
    val title: String,
    val description: String = "",
    val type: IssueType = IssueType.BUG,
    val status: IssueStatus = IssueStatus.OPEN,
    val priority: Priority = Priority.MEDIUM,
    val screen: String = "",
    val stepsToReproduce: String = "",
    val expectedBehavior: String = "",
    val actualBehavior: String = "",
    val technicalDetails: String = "",
    val exceptionType: String = "",
    val errorMessage: String = "",
    val sourceFile: String = "",
    val sourceLine: String = "",
    val suspectedCause: String = "",
    val solution: String = "",
    val verification: String = "",
    val introducedVersionId: Long? = null,
    val fixedVersionId: Long? = null,
    val environmentId: Long? = null,
    val testingSessionId: Long? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val resolvedAt: Instant? = null
)
