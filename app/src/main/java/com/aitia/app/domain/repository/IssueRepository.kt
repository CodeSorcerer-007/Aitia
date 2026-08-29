package com.aitia.app.domain.repository

import com.aitia.app.domain.model.AnalyticsSummary
import com.aitia.app.domain.model.Attachment
import com.aitia.app.domain.model.ChecklistItem
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueNote
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.RelatedIssue
import com.aitia.app.domain.model.RelationshipType
import com.aitia.app.domain.model.Tag
import com.aitia.app.domain.model.TimelineEvent
import kotlinx.coroutines.flow.Flow

interface IssueRepository {
    fun getAllIssues(): Flow<List<Issue>>
    fun getActiveIssues(): Flow<List<Issue>>
    fun getArchivedIssues(): Flow<List<Issue>>
    fun getPinnedIssues(): Flow<List<Issue>>
    fun getRecentIssues(limit: Int = 10): Flow<List<Issue>>
    fun searchIssues(query: String): Flow<List<Issue>>
    fun getIssuesForProject(projectId: Long): Flow<List<Issue>>
    fun getIssuesForSession(sessionId: Long): Flow<List<Issue>>

    suspend fun getIssueById(id: Long): Issue?
    fun observeIssueById(id: Long): Flow<Issue?>

    suspend fun saveIssue(issue: Issue): Long
    suspend fun deleteIssue(issue: Issue)
    suspend fun togglePinned(id: Long, isPinned: Boolean)
    suspend fun toggleArchived(id: Long, isArchived: Boolean)
    suspend fun updateStatus(id: Long, newStatus: IssueStatus)

    // Notes
    fun getNotesForIssue(issueId: Long): Flow<List<IssueNote>>
    suspend fun addNote(issueId: Long, text: String): Long
    suspend fun deleteNote(noteId: Long, issueId: Long, text: String)

    // Attachments
    fun getAttachmentsForIssue(issueId: Long): Flow<List<Attachment>>
    suspend fun addAttachment(issueId: Long, uriPath: String, filename: String, mimeType: String, sizeBytes: Long): Long
    suspend fun deleteAttachment(attachment: Attachment)

    // Checklist
    fun getChecklistForIssue(issueId: Long): Flow<List<ChecklistItem>>
    suspend fun addChecklistItem(issueId: Long, text: String): Long
    suspend fun toggleChecklistItem(id: Long, isCompleted: Boolean)
    suspend fun deleteChecklistItem(item: ChecklistItem)

    // Related Issues
    fun getRelatedIssues(issueId: Long): Flow<List<RelatedIssue>>
    suspend fun addRelatedIssue(sourceId: Long, targetId: Long, relationshipType: RelationshipType): Long

    // Tags
    fun getAllTags(): Flow<List<Tag>>
    fun getTagsForIssue(issueId: Long): Flow<List<Tag>>
    suspend fun addTagToIssue(issueId: Long, tagName: String)
    suspend fun removeTagFromIssue(issueId: Long, tagId: Long)

    // Timeline
    fun getTimelineForIssue(issueId: Long): Flow<List<TimelineEvent>>

    // Analytics
    fun getAnalyticsSummary(): Flow<AnalyticsSummary>
}
