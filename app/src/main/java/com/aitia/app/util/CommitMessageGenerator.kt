package com.aitia.app.util

import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueType

object CommitMessageGenerator {

    fun generate(issue: Issue, branchName: String? = null): String {
        val typePrefix = when (issue.type) {
            IssueType.CRASH -> "fix"
            IssueType.BUG -> "fix"
            IssueType.ERROR -> "fix"
            IssueType.PERFORMANCE -> "perf"
            IssueType.SECURITY -> "fix"
            IssueType.UI_UX -> "style"
            IssueType.FEATURE_IMPROVEMENT -> "feat"
            IssueType.TEST_OBSERVATION -> "test"
            IssueType.OTHER -> "chore"
        }

        val scope = issue.screen.ifEmpty {
            issue.projectName?.lowercase()?.replace("\\s+".toRegex(), "-") ?: "app"
        }.lowercase().replace("\\s+".toRegex(), "-")

        val cleanTitle = issue.title.trim().replaceFirstChar { it.lowercase() }.removeSuffix(".")

        return buildString {
            appendLine("$typePrefix($scope): $cleanTitle (fixes #${issue.id})")
            appendLine()
            if (issue.suspectedCause.isNotBlank()) {
                appendLine("Root Cause: ${issue.suspectedCause.trim()}")
            }
            if (issue.solution.isNotBlank()) {
                appendLine("Solution: ${issue.solution.trim()}")
            }
            if (issue.verification.isNotBlank()) {
                appendLine("Verification: ${issue.verification.trim()}")
            }
            if (issue.sourceFile.isNotBlank()) {
                appendLine("Source: ${issue.sourceFile}${if (issue.sourceLine.isNotBlank()) ":${issue.sourceLine}" else ""}")
            }
            if (!branchName.isNullOrBlank()) {
                appendLine("Branch: $branchName")
            }
            appendLine()
            appendLine("Aitia-Issue-ID: #${issue.id}")
            if (!issue.projectName.isNullOrBlank()) {
                appendLine("Aitia-Project: ${issue.projectName}")
            }
        }.trim()
    }
}
