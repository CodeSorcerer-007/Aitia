package com.aitia.app

import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.similarity.PreviousFixMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class PreviousFixMatcherTest {

    @Test
    fun testFindSimilarResolvedFixesByException() {
        val currentIssue = Issue(
            id = 10L,
            title = "App crashes on launch with SecurityException",
            type = IssueType.CRASH,
            priority = Priority.HIGH,
            status = IssueStatus.OPEN,
            exceptionType = "SecurityException",
            screen = "CameraPreview",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val previousResolved = Issue(
            id = 2L,
            title = "SecurityException when opening camera in settings",
            type = IssueType.CRASH,
            priority = Priority.HIGH,
            status = IssueStatus.FIXED,
            exceptionType = "SecurityException",
            solution = "Added CAMERA permission check in AndroidManifest and runtime request",
            screen = "Camera",
            createdAt = Instant.now().minusSeconds(86400),
            updatedAt = Instant.now().minusSeconds(86400)
        )

        val unResolvedIssue = Issue(
            id = 3L,
            title = "Random UI bug",
            type = IssueType.UI_UX,
            priority = Priority.LOW,
            status = IssueStatus.OPEN,
            solution = "",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val matches = PreviousFixMatcher.findSimilarResolvedFixes(
            currentIssue = currentIssue,
            allIssues = listOf(currentIssue, previousResolved, unResolvedIssue)
        )

        assertTrue(matches.isNotEmpty())
        assertEquals(2L, matches.first().issue.id)
        assertTrue(matches.first().similarityScore >= 0.45f)
        assertTrue(matches.first().matchReason.contains("SecurityException"))
    }
}
