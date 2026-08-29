package com.aitia.app.domain.model

enum class InsightType {
    HOTSPOT,
    CRITICAL_ALERT,
    REGRESSION_RISK,
    SESSION_TREND,
    POSITIVE_PROGRESS
}

data class InsightItem(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType,
    val actionableIssueId: Long? = null,
    val actionableProjectId: Long? = null
)
