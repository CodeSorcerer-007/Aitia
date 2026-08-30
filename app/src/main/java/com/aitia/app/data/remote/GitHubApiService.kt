package com.aitia.app.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

@Serializable
data class GitHubIssueRequest(
    val title: String,
    val body: String,
    val labels: List<String> = emptyList()
)

@Serializable
data class GitHubIssueResponse(
    val id: Long,
    val number: Int,
    val title: String,
    val html_url: String
)

interface GitHubApiService {
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String = "application/vnd.github.v3+json",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body request: GitHubIssueRequest
    ): GitHubIssueResponse
}
