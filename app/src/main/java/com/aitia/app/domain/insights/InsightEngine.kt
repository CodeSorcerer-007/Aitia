package com.aitia.app.domain.insights

import com.aitia.app.domain.model.InsightItem
import com.aitia.app.domain.model.InsightType
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.TestingSession

object InsightEngine {

    fun generateInsights(
        issues: List<Issue>,
        sessions: List<TestingSession>
    ): List<InsightItem> {
        val insights = mutableListOf<InsightItem>()
        if (issues.isEmpty()) return emptyList()

        val openIssues = issues.filter { !it.isResolved && !it.isArchived }
        val criticalOpen = openIssues.filter { it.priority == Priority.CRITICAL }
        val crashes = issues.filter { it.type == IssueType.CRASH }

        // 1. Critical Unresolved Bugs Alert
        if (criticalOpen.isNotEmpty()) {
            insights.add(
                InsightItem(
                    id = "critical_open_alert",
                    title = "${criticalOpen.size} Critical Unresolved ${if (criticalOpen.size == 1) "Issue" else "Issues"}",
                    description = "High-risk defects are currently open. Prioritize resolving '${criticalOpen.first().title.take(35)}...'",
                    type = InsightType.CRITICAL_ALERT,
                    actionableIssueId = criticalOpen.first().id
                )
            )
        }

        // 2. Crash Hotspot Analysis
        if (crashes.isNotEmpty()) {
            val mostCommonScreen = crashes.map { it.screen.ifBlank { "Unassigned Screen" } }
                .groupBy { it }
                .maxByOrNull { it.value.size }

            if (mostCommonScreen != null && mostCommonScreen.value.size >= 2) {
                insights.add(
                    InsightItem(
                        id = "crash_hotspot",
                        title = "Crash Hotspot: ${mostCommonScreen.key}",
                        description = "${mostCommonScreen.value.size} crashes originated from the ${mostCommonScreen.key} flow.",
                        type = InsightType.HOTSPOT
                    )
                )
            }
        }

        // 3. Issue Category Breakdown
        val topType = issues.groupBy { it.type }.maxByOrNull { it.value.size }
        if (topType != null) {
            val percentage = (topType.value.size.toFloat() / issues.size.toFloat() * 100).toInt()
            insights.add(
                InsightItem(
                    id = "top_category",
                    title = "Primary Defect Category: ${topType.key.displayName}",
                    description = "${topType.key.displayName} accounts for $percentage% (${topType.value.size}) of all recorded problems.",
                    type = InsightType.REGRESSION_RISK
                )
            )
        }

        // 4. Testing Session Trends
        if (sessions.isNotEmpty()) {
            val activeSession = sessions.firstOrNull { it.isActive }
            if (activeSession != null) {
                insights.add(
                    InsightItem(
                        id = "active_session_running",
                        title = "Active Session: ${activeSession.name}",
                        description = "Currently recording observations. Duration: ${activeSession.formattedDuration}.",
                        type = InsightType.SESSION_TREND
                    )
                )
            }
        }

        // 5. Positive Resolution Momentum
        val verifiedCount = issues.count { it.status.name == "VERIFIED" }
        if (verifiedCount > 0) {
            insights.add(
                InsightItem(
                    id = "verified_momentum",
                    title = "$verifiedCount Verified ${if (verifiedCount == 1) "Resolution" else "Resolutions"}",
                    description = "Fixes verified and closed with recorded engineering verification notes.",
                    type = InsightType.POSITIVE_PROGRESS
                )
            )
        }

        return insights
    }
}
