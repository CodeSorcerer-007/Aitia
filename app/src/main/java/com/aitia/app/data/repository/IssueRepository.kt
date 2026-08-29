package com.aitia.app.data.repository

import com.aitia.app.data.local.dao.AttachmentDao
import com.aitia.app.data.local.dao.ChecklistDao
import com.aitia.app.data.local.dao.EnvironmentDao
import com.aitia.app.data.local.dao.IssueDao
import com.aitia.app.data.local.dao.NoteDao
import com.aitia.app.data.local.dao.ProjectDao
import com.aitia.app.data.local.dao.RelatedIssueDao
import com.aitia.app.data.local.dao.TagDao
import com.aitia.app.data.local.dao.TestingSessionDao
import com.aitia.app.data.local.dao.TimelineDao
import com.aitia.app.data.local.entity.AttachmentEntity
import com.aitia.app.data.local.entity.ChecklistItemEntity
import com.aitia.app.data.local.entity.IssueEntity
import com.aitia.app.data.local.entity.IssueNoteEntity
import com.aitia.app.data.local.entity.IssueTagCrossRef
import com.aitia.app.data.local.entity.IssueTimelineEventEntity
import com.aitia.app.data.local.entity.RelatedIssueEntity
import com.aitia.app.data.local.entity.TagEntity
import com.aitia.app.domain.model.AnalyticsSummary
import com.aitia.app.domain.model.Attachment
import com.aitia.app.domain.model.ChecklistItem
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueNote
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.RelatedIssue
import com.aitia.app.domain.model.RelationshipType
import com.aitia.app.domain.model.Tag
import com.aitia.app.domain.model.TimelineEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant

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

class IssueRepositoryImpl(
    private val issueDao: IssueDao,
    private val projectDao: ProjectDao,
    private val sessionDao: TestingSessionDao,
    private val environmentDao: EnvironmentDao,
    private val noteDao: NoteDao,
    private val attachmentDao: AttachmentDao,
    private val tagDao: TagDao,
    private val relatedDao: RelatedIssueDao,
    private val checklistDao: ChecklistDao,
    private val timelineDao: TimelineDao
) : IssueRepository {

    override fun getAllIssues(): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.getAllIssues())

    override fun getActiveIssues(): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.getActiveIssues())

    override fun getArchivedIssues(): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.getArchivedIssues())

    override fun getPinnedIssues(): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.getPinnedIssues())

    override fun getRecentIssues(limit: Int): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.getRecentIssues(limit))

    override fun searchIssues(query: String): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.searchIssues(query))

    override fun getIssuesForProject(projectId: Long): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.getIssuesForProject(projectId))

    override fun getIssuesForSession(sessionId: Long): Flow<List<Issue>> = mapEntitiesToDomain(issueDao.getIssuesForSession(sessionId))

    private fun mapEntitiesToDomain(source: Flow<List<IssueEntity>>): Flow<List<Issue>> {
        return combine(
            source,
            projectDao.getAllProjects(),
            environmentDao.getAllEnvironments(),
            sessionDao.getAllSessions()
        ) { issueEntities, projects, envs, sessions ->
            issueEntities.map { entity ->
                val project = projects.firstOrNull { it.id == entity.projectId }
                val env = envs.firstOrNull { it.id == entity.environmentId }
                val session = sessions.firstOrNull { it.id == entity.testingSessionId }

                Issue(
                    id = entity.id,
                    projectId = entity.projectId,
                    projectName = project?.name,
                    title = entity.title,
                    description = entity.description,
                    type = entity.type,
                    status = entity.status,
                    priority = entity.priority,
                    screen = entity.screen,
                    stepsToReproduce = entity.stepsToReproduce,
                    expectedBehavior = entity.expectedBehavior,
                    actualBehavior = entity.actualBehavior,
                    technicalDetails = entity.technicalDetails,
                    exceptionType = entity.exceptionType,
                    errorMessage = entity.errorMessage,
                    sourceFile = entity.sourceFile,
                    sourceLine = entity.sourceLine,
                    suspectedCause = entity.suspectedCause,
                    solution = entity.solution,
                    verification = entity.verification,
                    introducedVersionId = entity.introducedVersionId,
                    fixedVersionId = entity.fixedVersionId,
                    environmentId = entity.environmentId,
                    environmentName = env?.name,
                    testingSessionId = entity.testingSessionId,
                    testingSessionName = session?.name,
                    isPinned = entity.isPinned,
                    isArchived = entity.isArchived,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    resolvedAt = entity.resolvedAt
                )
            }
        }
    }

    override suspend fun getIssueById(id: Long): Issue? {
        val entity = issueDao.getIssueById(id) ?: return null
        val project = entity.projectId?.let { projectDao.getProjectById(it) }
        val env = entity.environmentId?.let { environmentDao.getEnvironmentById(it) }
        val session = entity.testingSessionId?.let { sessionDao.getSessionById(it) }

        return Issue(
            id = entity.id,
            projectId = entity.projectId,
            projectName = project?.name,
            title = entity.title,
            description = entity.description,
            type = entity.type,
            status = entity.status,
            priority = entity.priority,
            screen = entity.screen,
            stepsToReproduce = entity.stepsToReproduce,
            expectedBehavior = entity.expectedBehavior,
            actualBehavior = entity.actualBehavior,
            technicalDetails = entity.technicalDetails,
            exceptionType = entity.exceptionType,
            errorMessage = entity.errorMessage,
            sourceFile = entity.sourceFile,
            sourceLine = entity.sourceLine,
            suspectedCause = entity.suspectedCause,
            solution = entity.solution,
            verification = entity.verification,
            introducedVersionId = entity.introducedVersionId,
            fixedVersionId = entity.fixedVersionId,
            environmentId = entity.environmentId,
            environmentName = env?.name,
            testingSessionId = entity.testingSessionId,
            testingSessionName = session?.name,
            isPinned = entity.isPinned,
            isArchived = entity.isArchived,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            resolvedAt = entity.resolvedAt
        )
    }

    override fun observeIssueById(id: Long): Flow<Issue?> {
        return combine(
            issueDao.observeIssueById(id),
            projectDao.getAllProjects(),
            environmentDao.getAllEnvironments(),
            sessionDao.getAllSessions()
        ) { entity, projects, envs, sessions ->
            entity?.let {
                val project = projects.firstOrNull { p -> p.id == it.projectId }
                val env = envs.firstOrNull { e -> e.id == it.environmentId }
                val session = sessions.firstOrNull { s -> s.id == it.testingSessionId }

                Issue(
                    id = it.id,
                    projectId = it.projectId,
                    projectName = project?.name,
                    title = it.title,
                    description = it.description,
                    type = it.type,
                    status = it.status,
                    priority = it.priority,
                    screen = it.screen,
                    stepsToReproduce = it.stepsToReproduce,
                    expectedBehavior = it.expectedBehavior,
                    actualBehavior = it.actualBehavior,
                    technicalDetails = it.technicalDetails,
                    exceptionType = it.exceptionType,
                    errorMessage = it.errorMessage,
                    sourceFile = it.sourceFile,
                    sourceLine = it.sourceLine,
                    suspectedCause = it.suspectedCause,
                    solution = it.solution,
                    verification = it.verification,
                    introducedVersionId = it.introducedVersionId,
                    fixedVersionId = it.fixedVersionId,
                    environmentId = it.environmentId,
                    environmentName = env?.name,
                    testingSessionId = it.testingSessionId,
                    testingSessionName = session?.name,
                    isPinned = it.isPinned,
                    isArchived = it.isArchived,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    resolvedAt = it.resolvedAt
                )
            }
        }
    }

    override suspend fun saveIssue(issue: Issue): Long {
        val isNew = issue.id == 0L
        val resolvedAt = if (issue.status.isResolved && issue.resolvedAt == null) Instant.now() else issue.resolvedAt
        val entity = IssueEntity(
            id = issue.id,
            projectId = issue.projectId,
            title = issue.title.trim(),
            description = issue.description.trim(),
            type = issue.type,
            status = issue.status,
            priority = issue.priority,
            screen = issue.screen.trim(),
            stepsToReproduce = issue.stepsToReproduce.trim(),
            expectedBehavior = issue.expectedBehavior.trim(),
            actualBehavior = issue.actualBehavior.trim(),
            technicalDetails = issue.technicalDetails.trim(),
            exceptionType = issue.exceptionType.trim(),
            errorMessage = issue.errorMessage.trim(),
            sourceFile = issue.sourceFile.trim(),
            sourceLine = issue.sourceLine.trim(),
            suspectedCause = issue.suspectedCause.trim(),
            solution = issue.solution.trim(),
            verification = issue.verification.trim(),
            introducedVersionId = issue.introducedVersionId,
            fixedVersionId = issue.fixedVersionId,
            environmentId = issue.environmentId,
            testingSessionId = issue.testingSessionId,
            isPinned = issue.isPinned,
            isArchived = issue.isArchived,
            createdAt = if (isNew) Instant.now() else issue.createdAt,
            updatedAt = Instant.now(),
            resolvedAt = resolvedAt
        )

        return if (isNew) {
            val newId = issueDao.insertIssue(entity)
            timelineDao.insertTimelineEvent(
                IssueTimelineEventEntity(
                    issueId = newId,
                    eventType = "CREATED",
                    title = "Issue Created",
                    description = "Created as ${issue.type.displayName} with ${issue.priority.displayName} priority."
                )
            )
            newId
        } else {
            val old = issueDao.getIssueById(issue.id)
            issueDao.updateIssue(entity)
            if (old != null && old.status != issue.status) {
                timelineDao.insertTimelineEvent(
                    IssueTimelineEventEntity(
                        issueId = issue.id,
                        eventType = "STATUS_CHANGED",
                        title = "Status changed to ${issue.status.displayName}",
                        description = "Previous status: ${old.status.displayName}"
                    )
                )
            }
            if (old != null && old.solution.isBlank() && issue.solution.isNotBlank()) {
                timelineDao.insertTimelineEvent(
                    IssueTimelineEventEntity(
                        issueId = issue.id,
                        eventType = "FIX_RECORDED",
                        title = "Fix Recorded",
                        description = issue.solution.take(100)
                    )
                )
            }
            issue.id
        }
    }

    override suspend fun deleteIssue(issue: Issue) {
        val entity = IssueEntity(
            id = issue.id,
            projectId = issue.projectId,
            title = issue.title,
            description = issue.description,
            type = issue.type,
            status = issue.status,
            priority = issue.priority,
            createdAt = issue.createdAt,
            updatedAt = issue.updatedAt
        )
        issueDao.deleteIssue(entity)
        noteDao.deleteNotesForIssue(issue.id)
        attachmentDao.deleteAttachmentsForIssue(issue.id)
        checklistDao.deleteChecklistForIssue(issue.id)
        timelineDao.deleteTimelineForIssue(issue.id)
        tagDao.clearTagsForIssue(issue.id)
    }

    override suspend fun togglePinned(id: Long, isPinned: Boolean) {
        issueDao.updatePinned(id, isPinned, Instant.now().toEpochMilli())
    }

    override suspend fun toggleArchived(id: Long, isArchived: Boolean) {
        issueDao.updateArchived(id, isArchived, Instant.now().toEpochMilli())
    }

    override suspend fun updateStatus(id: Long, newStatus: IssueStatus) {
        val issue = issueDao.getIssueById(id) ?: return
        val resolvedAt = if (newStatus.isResolved) Instant.now() else null
        val updated = issue.copy(
            status = newStatus,
            updatedAt = Instant.now(),
            resolvedAt = resolvedAt
        )
        issueDao.updateIssue(updated)
        timelineDao.insertTimelineEvent(
            IssueTimelineEventEntity(
                issueId = id,
                eventType = "STATUS_CHANGED",
                title = "Status updated to ${newStatus.displayName}",
                description = "Moved from ${issue.status.displayName} to ${newStatus.displayName}"
            )
        )
    }

    override fun getNotesForIssue(issueId: Long): Flow<List<IssueNote>> {
        return noteDao.getNotesForIssue(issueId).map { list ->
            list.map { IssueNote(it.id, it.issueId, it.text, it.createdAt) }
        }
    }

    override suspend fun addNote(issueId: Long, text: String): Long {
        val id = noteDao.insertNote(IssueNoteEntity(issueId = issueId, text = text.trim()))
        timelineDao.insertTimelineEvent(
            IssueTimelineEventEntity(
                issueId = issueId,
                eventType = "NOTE_ADDED",
                title = "Investigation Note Added",
                description = text.take(80)
            )
        )
        return id
    }

    override suspend fun deleteNote(noteId: Long, issueId: Long, text: String) {
        noteDao.deleteNote(IssueNoteEntity(id = noteId, issueId = issueId, text = text))
    }

    override fun getAttachmentsForIssue(issueId: Long): Flow<List<Attachment>> {
        return attachmentDao.getAttachmentsForIssue(issueId).map { list ->
            list.map { Attachment(it.id, it.issueId, it.uriPath, it.filename, it.mimeType, it.sizeBytes, it.createdAt) }
        }
    }

    override suspend fun addAttachment(
        issueId: Long,
        uriPath: String,
        filename: String,
        mimeType: String,
        sizeBytes: Long
    ): Long {
        val id = attachmentDao.insertAttachment(
            AttachmentEntity(
                issueId = issueId,
                uriPath = uriPath,
                filename = filename,
                mimeType = mimeType,
                sizeBytes = sizeBytes
            )
        )
        timelineDao.insertTimelineEvent(
            IssueTimelineEventEntity(
                issueId = issueId,
                eventType = "ATTACHMENT_ADDED",
                title = "Attachment Added: $filename"
            )
        )
        return id
    }

    override suspend fun deleteAttachment(attachment: Attachment) {
        attachmentDao.deleteAttachment(
            AttachmentEntity(
                id = attachment.id,
                issueId = attachment.issueId,
                uriPath = attachment.uriPath,
                filename = attachment.filename,
                mimeType = attachment.mimeType,
                sizeBytes = attachment.sizeBytes,
                createdAt = attachment.createdAt
            )
        )
    }

    override fun getChecklistForIssue(issueId: Long): Flow<List<ChecklistItem>> {
        return checklistDao.getChecklistForIssue(issueId).map { list ->
            list.map { ChecklistItem(it.id, it.issueId, it.text, it.isCompleted, it.position) }
        }
    }

    override suspend fun addChecklistItem(issueId: Long, text: String): Long {
        return checklistDao.insertChecklistItem(
            ChecklistItemEntity(
                issueId = issueId,
                text = text.trim()
            )
        )
    }

    override suspend fun toggleChecklistItem(id: Long, isCompleted: Boolean) {
        checklistDao.toggleCompleted(id, isCompleted)
    }

    override suspend fun deleteChecklistItem(item: ChecklistItem) {
        checklistDao.deleteChecklistItem(
            ChecklistItemEntity(
                id = item.id,
                issueId = item.issueId,
                text = item.text,
                isCompleted = item.isCompleted,
                position = item.position
            )
        )
    }

    override fun getRelatedIssues(issueId: Long): Flow<List<RelatedIssue>> {
        return combine(
            relatedDao.getRelatedIssues(issueId),
            issueDao.getAllIssues()
        ) { relations, issues ->
            relations.map { rel ->
                val targetId = if (rel.sourceIssueId == issueId) rel.targetIssueId else rel.sourceIssueId
                val targetIssue = issues.firstOrNull { it.id == targetId }
                RelatedIssue(
                    id = rel.id,
                    sourceIssueId = rel.sourceIssueId,
                    targetIssueId = targetId,
                    targetTitle = targetIssue?.title ?: "Unknown Issue #$targetId",
                    targetType = targetIssue?.type ?: IssueType.BUG,
                    targetStatus = targetIssue?.status ?: IssueStatus.OPEN,
                    relationshipType = rel.relationshipType
                )
            }
        }
    }

    override suspend fun addRelatedIssue(sourceId: Long, targetId: Long, relationshipType: RelationshipType): Long {
        return relatedDao.insertRelatedIssue(
            RelatedIssueEntity(
                sourceIssueId = sourceId,
                targetIssueId = targetId,
                relationshipType = relationshipType
            )
        )
    }

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { list -> list.map { Tag(it.id, it.name, it.colorHex) } }
    }

    override fun getTagsForIssue(issueId: Long): Flow<List<Tag>> {
        return tagDao.getTagsForIssue(issueId).map { list -> list.map { Tag(it.id, it.name, it.colorHex) } }
    }

    override suspend fun addTagToIssue(issueId: Long, tagName: String) {
        val cleanName = tagName.trim().lowercase()
        var tag = tagDao.getTagByName(cleanName)
        val tagId = if (tag != null) {
            tag.id
        } else {
            tagDao.insertTag(TagEntity(name = cleanName))
        }
        tagDao.insertIssueTagCrossRef(IssueTagCrossRef(issueId = issueId, tagId = tagId))
    }

    override suspend fun removeTagFromIssue(issueId: Long, tagId: Long) {
        tagDao.removeTagFromIssue(issueId, tagId)
    }

    override fun getTimelineForIssue(issueId: Long): Flow<List<TimelineEvent>> {
        return timelineDao.getTimelineForIssue(issueId).map { list ->
            list.map {
                TimelineEvent(
                    id = it.id,
                    issueId = it.issueId,
                    eventType = it.eventType,
                    title = it.title,
                    description = it.description,
                    timestamp = it.timestamp
                )
            }
        }
    }

    override fun getAnalyticsSummary(): Flow<AnalyticsSummary> {
        return combine(
            issueDao.getAllIssues(),
            projectDao.getAllProjects(),
            sessionDao.getAllSessions()
        ) { issues, projects, sessions ->
            val total = issues.size
            val open = issues.count { !it.isArchived && it.status == IssueStatus.OPEN }
            val investigating = issues.count { !it.isArchived && it.status == IssueStatus.INVESTIGATING }
            val fixed = issues.count { !it.isArchived && it.status == IssueStatus.FIXED }
            val verified = issues.count { !it.isArchived && it.status == IssueStatus.VERIFIED }
            val critical = issues.count { !it.isArchived && it.priority == Priority.CRITICAL && !it.status.isResolved }
            val high = issues.count { !it.isArchived && it.priority == Priority.HIGH && !it.status.isResolved }
            val crashes = issues.count { it.type == IssueType.CRASH }

            val byType = issues.groupBy { it.type }.mapValues { it.value.size }
            val byPriority = issues.groupBy { it.priority }.mapValues { it.value.size }
            val byStatus = issues.groupBy { it.status }.mapValues { it.value.size }

            val byProject = issues.groupBy { issue ->
                projects.firstOrNull { it.id == issue.projectId }?.name ?: "Unassigned"
            }.mapValues { it.value.size }

            val resolvedWithTimes = issues.filter { it.resolvedAt != null }
            val avgHours = if (resolvedWithTimes.isNotEmpty()) {
                val totalMinutes = resolvedWithTimes.sumOf {
                    Duration.between(it.createdAt, it.resolvedAt).toMinutes()
                }
                (totalMinutes.toDouble() / resolvedWithTimes.size.toDouble()) / 60.0
            } else 0.0

            AnalyticsSummary(
                totalIssues = total,
                openIssues = open,
                investigatingIssues = investigating,
                fixedIssues = fixed,
                verifiedIssues = verified,
                criticalIssues = critical,
                highIssues = high,
                crashCount = crashes,
                issuesByType = byType,
                issuesByPriority = byPriority,
                issuesByStatus = byStatus,
                issuesByProject = byProject,
                averageResolutionHours = avgHours,
                totalTestingSessions = sessions.size
            )
        }
    }
}
