package com.aitia.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Issues : Screen("issues")
    object Projects : Screen("projects")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Long) = "project_detail/$projectId"
    }
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object IssueDetail : Screen("issue_detail/{issueId}") {
        fun createRoute(issueId: Long) = "issue_detail/$issueId"
    }
    object Onboarding : Screen("onboarding")
    object SessionSummary : Screen("session_summary/{sessionId}") {
        fun createRoute(sessionId: Long) = "session_summary/$sessionId"
    }
}
