package com.aitia.app.util

import com.aitia.app.domain.model.Issue
import com.aitia.app.data.remote.GitHubApiService
import com.aitia.app.data.remote.GitHubIssueRequest
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class GitPullRequestPayload(
    val branchName: String,
    val prTitle: String,
    val prBodyMarkdown: String,
    val githubCliCommand: String,
    val gitCliSequence: String,
    val githubWebUrl: String,
    val gitlabWebUrl: String
)

object GitRemoteSyncManager {

    /**
     * Generates a complete GitHub & GitLab Pull Request package from an Aitia issue.
     */
    fun generatePullRequestPayload(
        issue: Issue,
        repoOwner: String = "CodeSorcerer-007",
        repoName: String = "Aitia",
        baseBranch: String = "main"
    ): GitPullRequestPayload {
        val sanitizedTitleSlug = issue.title.lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .take(35)

        val branchName = "fix/issue-${issue.id}-$sanitizedTitleSlug"

        val scope = when {
            issue.screen.isNotBlank() -> issue.screen.lowercase().replace(" ", "-").take(15)
            issue.sourceFile.isNotBlank() -> issue.sourceFile.substringBeforeLast(".").lowercase().take(15)
            else -> "core"
        }

        val prTitle = "fix($scope): ${issue.title} (Fixes #${issue.id})"

        val prBodyMarkdown = buildString {
            appendLine("## 📝 Summary of Changes")
            appendLine("Fixes #${issue.id} — **${issue.title}**")
            appendLine()
            if (issue.description.isNotBlank()) {
                appendLine("### Description")
                appendLine(issue.description)
                appendLine()
            }
            appendLine("## 🐛 Root Cause (Αἰτία)")
            appendLine(issue.suspectedCause.ifBlank { "Identified defect in `${issue.sourceFile.ifBlank { "component" }}` causing `${issue.exceptionType.ifBlank { "unexpected error" }}`." })
            appendLine()
            appendLine("## 🛠️ Solution Implemented")
            appendLine(issue.solution.ifBlank { "Applied defensive guards and updated state synchronization." })
            appendLine()
            if (issue.stepsToReproduce.isNotBlank()) {
                appendLine("## 🔁 Steps to Reproduce")
                appendLine(issue.stepsToReproduce)
                appendLine()
            }
            if (issue.verification.isNotBlank()) {
                appendLine("## 🧪 Verification & Multi-Device Matrix")
                appendLine(issue.verification)
                appendLine()
            }
            appendLine("---")
            appendLine("*Generated automatically by [Aitia Defect Workbench](https://github.com/$repoOwner/$repoName)*")
        }

        val escapedPrTitle = prTitle.replace("\"", "\\\"")
        val escapedPrBody = prBodyMarkdown.replace("\"", "\\\"").replace("\n", "\\n")

        val githubCliCommand = "gh pr create --title \"$escapedPrTitle\" --body \"$escapedPrBody\" --base \"$baseBranch\" --head \"$branchName\""

        val gitCliSequence = "git checkout -b $branchName && git add . && git commit -m \"$escapedPrTitle\" && git push origin $branchName"

        val encodedTitle = URLEncoder.encode(prTitle, StandardCharsets.UTF_8.toString())
        val encodedBody = URLEncoder.encode(prBodyMarkdown, StandardCharsets.UTF_8.toString())

        val githubWebUrl = "https://github.com/$repoOwner/$repoName/compare/$baseBranch...$branchName?expand=1&title=$encodedTitle&body=$encodedBody"
        val gitlabWebUrl = "https://gitlab.com/$repoOwner/$repoName/-/merge_requests/new?merge_request%5Bsource_branch%5D=$branchName&merge_request%5Btarget_branch%5D=$baseBranch&merge_request%5Btitle%5D=$encodedTitle&merge_request%5Bdescription%5D=$encodedBody"

        return GitPullRequestPayload(
            branchName = branchName,
            prTitle = prTitle,
            prBodyMarkdown = prBodyMarkdown,
            githubCliCommand = githubCliCommand,
            gitCliSequence = gitCliSequence,
            githubWebUrl = githubWebUrl,
            gitlabWebUrl = gitlabWebUrl
        )
    }

    /**
     * Instantly creates a GitHub Issue via REST API using the user's PAT.
     */
    suspend fun createGitHubIssue(
        issue: Issue,
        githubPat: String,
        defaultRepo: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (githubPat.isBlank() || defaultRepo.isBlank() || !defaultRepo.contains("/")) {
                return@withContext Result.failure(Exception("GitHub PAT or Default Repo is missing/invalid."))
            }

            val parts = defaultRepo.split("/")
            val owner = parts[0]
            val repo = parts[1]

            val json = Json { ignoreUnknownKeys = true }
            val contentType = "application/json".toMediaType()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()

            val api = retrofit.create(GitHubApiService::class.java)

            val prPayload = generatePullRequestPayload(issue, owner, repo)
            val request = GitHubIssueRequest(
                title = "Bug: ${issue.title}",
                body = prPayload.prBodyMarkdown,
                labels = listOf("bug", "aitia")
            )

            val response = api.createIssue(
                token = "Bearer $githubPat",
                owner = owner,
                repo = repo,
                request = request
            )
            
            Result.success(response.html_url)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
