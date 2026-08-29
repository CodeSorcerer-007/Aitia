package com.aitia.app.domain.model

data class AnalyticsSummary(
    val totalIssues: Int = 0,
    val openIssues: Int = 0,
    val investigatingIssues: Int = 0,
    val fixedIssues: Int = 0,
    val verifiedIssues: Int = 0,
    val criticalIssues: Int = 0,
    val highIssues: Int = 0,
    val crashCount: Int = 0,
    val issuesByType: Map<IssueType, Int> = emptyMap(),
    val issuesByPriority: Map<Priority, Int> = emptyMap(),
    val issuesByStatus: Map<IssueStatus, Int> = emptyMap(),
    val issuesByProject: Map<String, Int> = emptyMap(),
    val averageResolutionHours: Double = 0.0,
    val totalTestingSessions: Int = 0
)
