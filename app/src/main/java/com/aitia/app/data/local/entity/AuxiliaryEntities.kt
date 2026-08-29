package com.aitia.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aitia.app.domain.model.RelationshipType
import java.time.Instant

@Entity(
    tableName = "issue_notes",
    indices = [Index(value = ["issueId"])]
)
data class IssueNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issueId: Long,
    val text: String,
    val createdAt: Instant = Instant.now()
)

@Entity(
    tableName = "attachments",
    indices = [Index(value = ["issueId"])]
)
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issueId: Long,
    val uriPath: String,
    val filename: String,
    val mimeType: String = "image/png",
    val sizeBytes: Long = 0,
    val createdAt: Instant = Instant.now()
)

@Entity(
    tableName = "tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#58A6FF"
)

@Entity(
    tableName = "issue_tag_cross_ref",
    primaryKeys = ["issueId", "tagId"],
    indices = [Index(value = ["tagId"])]
)
data class IssueTagCrossRef(
    val issueId: Long,
    val tagId: Long
)

@Entity(
    tableName = "related_issues",
    indices = [
        Index(value = ["sourceIssueId"]),
        Index(value = ["targetIssueId"])
    ]
)
data class RelatedIssueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceIssueId: Long,
    val targetIssueId: Long,
    val relationshipType: RelationshipType = RelationshipType.RELATED_TO
)

@Entity(
    tableName = "checklist_items",
    indices = [Index(value = ["issueId"])]
)
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issueId: Long,
    val text: String,
    val isCompleted: Boolean = false,
    val position: Int = 0
)

@Entity(
    tableName = "issue_timeline_events",
    indices = [Index(value = ["issueId"])]
)
data class IssueTimelineEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issueId: Long,
    val eventType: String,
    val title: String,
    val description: String = "",
    val timestamp: Instant = Instant.now()
)
