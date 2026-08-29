package com.aitia.app

import com.aitia.app.domain.insights.InsightEngine
import com.aitia.app.domain.model.InsightType
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightEngineTest {

    @Test
    fun testGeneratesCriticalAlertWhenCriticalIssuesOpen() {
        val issues = listOf(
            Issue(
                id = 1,
                title = "Payment checkout ANR",
                priority = Priority.CRITICAL,
                type = IssueType.CRASH
            )
        )

        val insights = InsightEngine.generateInsights(issues, emptyList())

        assertTrue(insights.any { it.type == InsightType.CRITICAL_ALERT })
        assertEquals(1L, insights.first { it.type == InsightType.CRITICAL_ALERT }.actionableIssueId)
    }

    @Test
    fun testDetectsCrashHotspot() {
        val issues = listOf(
            Issue(id = 1, title = "Camera crash 1", type = IssueType.CRASH, screen = "Profile Screen"),
            Issue(id = 2, title = "Camera crash 2", type = IssueType.CRASH, screen = "Profile Screen")
        )

        val insights = InsightEngine.generateInsights(issues, emptyList())

        assertTrue(insights.any { it.type == InsightType.HOTSPOT && it.title.contains("Profile Screen") })
    }
}
