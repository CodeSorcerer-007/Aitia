package com.aitia.app

import com.aitia.app.domain.insights.SmartAutoTriageEngine
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartAutoTriageEngineTest {

    @Test
    fun testTriageInfersCrashTypeAndCriticalPriority() {
        val title = "Fatal NullPointerException crash on launch"
        val triage = SmartAutoTriageEngine.triage(title)

        assertNotNull(triage)
        assertEquals(IssueType.CRASH, triage.suggestedType)
        assertEquals(Priority.CRITICAL, triage.suggestedPriority)
        assertTrue(triage.suggestedInitialSteps.contains("App terminates unexpectedly"))
    }

    @Test
    fun testTriageInfersBillingAndUiTags() {
        val title = "Cannot checkout with credit card on payment dialog"
        val triage = SmartAutoTriageEngine.triage(title)

        assertNotNull(triage)
        assertTrue(triage.suggestedTags.contains("billing"))
        assertTrue(triage.suggestedTags.contains("ui"))
        assertEquals(Priority.CRITICAL, triage.suggestedPriority)
    }

    @Test
    fun testTriageInfersPerformanceType() {
        val title = "High battery drain and memory leak while scrolling"
        val triage = SmartAutoTriageEngine.triage(title)

        assertNotNull(triage)
        assertEquals(IssueType.PERFORMANCE, triage.suggestedType)
        assertTrue(triage.suggestedInitialSteps.contains("FPS drop"))
    }
}
