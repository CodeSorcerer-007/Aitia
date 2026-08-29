package com.aitia.app

import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.util.CommitMessageGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class CommitMessageGeneratorTest {

    @Test
    fun testGenerateConventionalCommitWithScope() {
        val issue = Issue(
            id = 42L,
            title = "App crashes on Profile picture save",
            type = IssueType.CRASH,
            priority = Priority.CRITICAL,
            status = IssueStatus.FIXED,
            screen = "Profile",
            suspectedCause = "Null safety violation on camera URI callback",
            solution = "Added null check and rememberLauncherForActivityResult validation",
            verification = "Verified on Pixel 8",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val commit = CommitMessageGenerator.generate(issue)

        assertTrue(commit.startsWith("fix(profile): app crashes on Profile picture save"))
        assertTrue(commit.contains("Root Cause:"))
        assertTrue(commit.contains("Solution:"))
        assertTrue(commit.contains("fixes #42"))
    }

    @Test
    fun testGenerateFeatureCommit() {
        val issue = Issue(
            id = 99L,
            title = "Add dark mode toggle in preferences",
            type = IssueType.FEATURE_IMPROVEMENT,
            priority = Priority.MEDIUM,
            status = IssueStatus.OPEN,
            screen = "Settings",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val commit = CommitMessageGenerator.generate(issue)

        assertTrue(commit.startsWith("feat(settings): add dark mode toggle in preferences"))
        assertTrue(commit.contains("fixes #99"))
    }
}
