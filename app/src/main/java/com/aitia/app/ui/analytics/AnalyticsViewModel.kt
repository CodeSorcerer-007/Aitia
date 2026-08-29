package com.aitia.app.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.TestingSessionRepository
import com.aitia.app.domain.insights.InsightEngine
import com.aitia.app.domain.model.AnalyticsSummary
import com.aitia.app.domain.model.InsightItem
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.TestingSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AnalyticsUiState(
    val summary: AnalyticsSummary = AnalyticsSummary(),
    val insights: List<InsightItem> = emptyList(),
    val totalIssues: Int = 0,
    val isLoading: Boolean = false
)

class AnalyticsViewModel(
    private val issueRepository: IssueRepository,
    private val sessionRepository: TestingSessionRepository
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = combine(
        issueRepository.getAllIssues(),
        sessionRepository.getAllSessions(),
        issueRepository.getAnalyticsSummary()
    ) { allIssues, sessions, summary ->
        val insights = InsightEngine.generateInsights(allIssues, sessions)
        AnalyticsUiState(
            summary = summary,
            insights = insights,
            totalIssues = allIssues.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState(isLoading = true)
    )
}
