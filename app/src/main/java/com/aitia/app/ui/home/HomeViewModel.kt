package com.aitia.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.data.preferences.UserPreferencesRepository
import com.aitia.app.domain.insights.InsightEngine
import com.aitia.app.domain.model.AnalyticsSummary
import com.aitia.app.domain.model.InsightItem
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.TestingSession
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.ProjectRepository
import com.aitia.app.domain.repository.TestingSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val recentIssues: List<Issue> = emptyList(),
    val pinnedIssues: List<Issue> = emptyList(),
    val allIssues: List<Issue> = emptyList(),
    val activeSession: TestingSession? = null,
    val analyticsSummary: AnalyticsSummary = AnalyticsSummary(),
    val insights: List<InsightItem> = emptyList(),
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val issueRepository: IssueRepository,
    private val projectRepository: ProjectRepository,
    private val sessionRepository: TestingSessionRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            issueRepository.getActiveIssues(),
            issueRepository.getPinnedIssues(),
            issueRepository.getAllIssues()
        ) { active, pinned, all -> Triple(active, pinned, all) },
        combine(
            sessionRepository.getActiveSession(),
            issueRepository.getAnalyticsSummary(),
            sessionRepository.getAllSessions(),
            projectRepository.getAllProjects()
        ) { activeSession, analytics, allSessions, projects ->
            Quad(activeSession, analytics, allSessions, projects)
        }
    ) { (activeIssues, pinned, allIssues), (activeSession, analytics, allSessions, projects) ->
        val insights = InsightEngine.generateInsights(activeIssues, allSessions)
        HomeUiState(
            recentIssues = activeIssues.take(6),
            pinnedIssues = pinned,
            allIssues = allIssues,
            activeSession = activeSession,
            analyticsSummary = analytics,
            insights = insights,
            projects = projects,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun stopActiveSession(session: TestingSession) {
        viewModelScope.launch {
            sessionRepository.stopSession(session.id)
            preferencesRepository.setActiveTestingSessionId(null)
        }
    }
}

private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
