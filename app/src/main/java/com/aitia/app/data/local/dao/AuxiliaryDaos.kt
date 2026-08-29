package com.aitia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aitia.app.data.local.entity.AttachmentEntity
import com.aitia.app.data.local.entity.ChecklistItemEntity
import com.aitia.app.data.local.entity.IssueNoteEntity
import com.aitia.app.data.local.entity.IssueTagCrossRef
import com.aitia.app.data.local.entity.IssueTimelineEventEntity
import com.aitia.app.data.local.entity.RelatedIssueEntity
import com.aitia.app.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM issue_notes WHERE issueId = :issueId ORDER BY createdAt ASC")
    fun getNotesForIssue(issueId: Long): Flow<List<IssueNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: IssueNoteEntity): Long

    @Delete
    suspend fun deleteNote(note: IssueNoteEntity)

    @Query("DELETE FROM issue_notes WHERE issueId = :issueId")
    suspend fun deleteNotesForIssue(issueId: Long)
}

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE issueId = :issueId ORDER BY createdAt DESC")
    fun getAttachmentsForIssue(issueId: Long): Flow<List<AttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

    @Query("DELETE FROM attachments WHERE issueId = :issueId")
    suspend fun deleteAttachmentsForIssue(issueId: Long)
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN issue_tag_cross_ref x ON t.id = x.tagId
        WHERE x.issueId = :issueId
    """)
    fun getTagsForIssue(issueId: Long): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIssueTagCrossRef(crossRef: IssueTagCrossRef)

    @Query("DELETE FROM issue_tag_cross_ref WHERE issueId = :issueId AND tagId = :tagId")
    suspend fun removeTagFromIssue(issueId: Long, tagId: Long)

    @Query("DELETE FROM issue_tag_cross_ref WHERE issueId = :issueId")
    suspend fun clearTagsForIssue(issueId: Long)
}

@Dao
interface RelatedIssueDao {
    @Query("SELECT * FROM related_issues WHERE sourceIssueId = :issueId OR targetIssueId = :issueId")
    fun getRelatedIssues(issueId: Long): Flow<List<RelatedIssueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelatedIssue(related: RelatedIssueEntity): Long

    @Delete
    suspend fun deleteRelatedIssue(related: RelatedIssueEntity)
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items WHERE issueId = :issueId ORDER BY position ASC, id ASC")
    fun getChecklistForIssue(issueId: Long): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItemEntity): Long

    @Update
    suspend fun updateChecklistItem(item: ChecklistItemEntity)

    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItemEntity)

    @Query("UPDATE checklist_items SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun toggleCompleted(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM checklist_items WHERE issueId = :issueId")
    suspend fun deleteChecklistForIssue(issueId: Long)
}

@Dao
interface TimelineDao {
    @Query("SELECT * FROM issue_timeline_events WHERE issueId = :issueId ORDER BY timestamp DESC")
    fun getTimelineForIssue(issueId: Long): Flow<List<IssueTimelineEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineEvent(event: IssueTimelineEventEntity): Long

    @Query("DELETE FROM issue_timeline_events WHERE issueId = :issueId")
    suspend fun deleteTimelineForIssue(issueId: Long)
}
