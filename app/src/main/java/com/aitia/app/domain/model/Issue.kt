package com.aitia.app.domain.model

import java.time.Instant

data class Issue(
    val id: Long = 0,
    val projectId: Long? = null,
    val projectName: String? = null,
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
    val introducedVersionName: String? = null,
    val fixedVersionId: Long? = null,
    val fixedVersionName: String? = null,
    val environmentId: Long? = null,
    val environmentName: String? = null,
    val testingSessionId: Long? = null,
    val testingSessionName: String? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val resolvedAt: Instant? = null,
    val tags: List<Tag> = emptyList(),
    val notesCount: Int = 0,
    val attachmentsCount: Int = 0,
    val checklistTotal: Int = 0,
    val checklistCompleted: Int = 0
) {
    val isResolved: Boolean
        get() = status.isResolved

    val checklistProgress: Float
        get() = if (checklistTotal > 0) checklistCompleted.toFloat() / checklistTotal.toFloat() else 0f
}
