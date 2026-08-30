package com.aitia.app

import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.util.GitRemoteSyncManager
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GitRemoteSyncManagerTest {

    @Test
    fun testGeneratePullRequestPayloadCreatesValidCommandsAndMarkdown() {
        val issue = Issue(
            id = 55,
            title = "Fix camera preview freeze on foldables",
            description = "Camera freezes when Samsung Galaxy Fold is unfolded.",
            type = IssueType.BUG,
            priority = Priority.HIGH,
            status = IssueStatus.INVESTIGATING,
            screen = "Camera Screen",
            sourceFile = "CameraCaptureDialog.kt",
            suspectedCause = "Surface lifecycle destroyed during fold transition",
            solution = "Re-bind CameraProvider on configuration change",
            stepsToReproduce = "1. Open camera\n2. Unfold phone\n3. Observe freeze",
            verification = "Galaxy Fold 5: PASS"
        )

        val payload = GitRemoteSyncManager.generatePullRequestPayload(
            issue = issue,
            repoOwner = "CodeSorcerer-007",
            repoName = "Aitia",
            baseBranch = "main"
        )

        assertNotNull(payload)
        assertTrue(payload.branchName.startsWith("fix/issue-55-"))
        assertTrue(payload.prTitle.contains("Fixes #55"))
        assertTrue(payload.prBodyMarkdown.contains("## 📝 Summary of Changes"))
        assertTrue(payload.prBodyMarkdown.contains("## 🐛 Root Cause (Αἰτία)"))
        assertTrue(payload.prBodyMarkdown.contains("## 🛠️ Solution Implemented"))
        assertTrue(payload.githubCliCommand.startsWith("gh pr create"))
        assertTrue(payload.gitCliSequence.contains("git checkout -b"))
        assertTrue(payload.githubWebUrl.contains("github.com/CodeSorcerer-007/Aitia/compare"))
    }
}
