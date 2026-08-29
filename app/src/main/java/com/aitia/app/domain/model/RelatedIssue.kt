package com.aitia.app.domain.model

data class RelatedIssue(
    val id: Long = 0,
    val sourceIssueId: Long,
    val targetIssueId: Long,
    val targetTitle: String = "",
    val targetType: IssueType = IssueType.BUG,
    val targetStatus: IssueStatus = IssueStatus.OPEN,
    val relationshipType: RelationshipType = RelationshipType.RELATED_TO
)
