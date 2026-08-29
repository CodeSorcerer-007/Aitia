package com.aitia.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.data.preferences.UserPreferencesRepository
import com.aitia.app.data.repository.IssueRepository
import com.aitia.app.data.repository.ProjectRepository
import com.aitia.app.data.repository.TestingSessionRepository
import com.aitia.app.domain.insights.InsightEngine
import com.aitia.app.domain.model.AnalyticsSummary
import com.aitia.app.domain.model.InsightItem
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.TestingSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val recentIssues: List<Issue> = emptyList(),
    val pinnedIssues: List<Issue> = emptyList(),
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
            sessionRepository.getActiveSession()
        ) { active, pinned, session -> Triple(active, pinned, session) },
        combine(
            issueRepository.getAnalyticsSummary(),
            sessionRepository.getAllSessions(),
            projectRepository.getAllProjects()
        ) { analytics, allSessions, projects -> Triple(analytics, allSessions, projects) }
    ) { (activeIssues, pinned, activeSession), (analytics, allSessions, projects) ->
        val insights = InsightEngine.generateInsights(activeIssues, allSessions)
        HomeUiState(
            recentIssues = activeIssues.take(6),
            pinnedIssues = pinned,
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
