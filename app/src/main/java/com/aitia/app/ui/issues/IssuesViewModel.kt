package com.aitia.app.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.data.repository.IssueRepository
import com.aitia.app.data.repository.ProjectRepository
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.Project
import com.aitia.app.ui.components.IssueFilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class IssueSortOption(val displayName: String) {
    RECENTLY_UPDATED("Recently Updated"),
    RECENTLY_CREATED("Recently Created"),
    HIGHEST_PRIORITY("Highest Priority"),
    OLDEST_UNRESOLVED("Oldest Unresolved")
}

data class IssuesUiState(
    val searchQuery: String = "",
    val filterState: IssueFilterState = IssueFilterState(),
    val sortOption: IssueSortOption = IssueSortOption.RECENTLY_CREATED,
    val issues: List<Issue> = emptyList(),
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false
)

class IssuesViewModel(
    private val issueRepository: IssueRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterState = MutableStateFlow(IssueFilterState())
    private val _sortOption = MutableStateFlow(IssueSortOption.RECENTLY_CREATED)

    val uiState: StateFlow<IssuesUiState> = combine(
        _searchQuery,
        _filterState,
        _sortOption,
        issueRepository.getAllIssues(),
        projectRepository.getAllProjects()
    ) { query: String, filter: IssueFilterState, sort: IssueSortOption, allIssues: List<Issue>, projects: List<Project> ->
        val filtered: List<Issue> = allIssues.filter { issue: Issue ->
            // Archive filter
            val archiveMatch = if (filter.showArchived) issue.isArchived else !issue.isArchived

            // Search query match
            val searchMatch = query.isBlank() ||
                    issue.title.contains(query, ignoreCase = true) ||
                    issue.description.contains(query, ignoreCase = true) ||
                    issue.screen.contains(query, ignoreCase = true) ||
                    issue.technicalDetails.contains(query, ignoreCase = true) ||
                    issue.exceptionType.contains(query, ignoreCase = true) ||
                    issue.errorMessage.contains(query, ignoreCase = true) ||
                    issue.suspectedCause.contains(query, ignoreCase = true) ||
                    issue.solution.contains(query, ignoreCase = true) ||
                    (issue.projectName?.contains(query, ignoreCase = true) == true)

            // Status filter
            val statusMatch = filter.selectedStatus == null || issue.status == filter.selectedStatus

            // Priority filter
            val priorityMatch = filter.selectedPriority == null || issue.priority == filter.selectedPriority

            // Type filter
            val typeMatch = filter.selectedType == null || issue.type == filter.selectedType

            // Project filter
            val projectMatch = filter.selectedProjectId == null || issue.projectId == filter.selectedProjectId

            // Pinned filter
            val pinnedMatch = !filter.onlyPinned || issue.isPinned

            archiveMatch && searchMatch && statusMatch && priorityMatch && typeMatch && projectMatch && pinnedMatch
        }

        // Sorting
        val sorted: List<Issue> = when (sort) {
            IssueSortOption.RECENTLY_CREATED -> filtered.sortedWith(
                compareByDescending<Issue> { it.isPinned }.thenByDescending { it.createdAt }
            )
            IssueSortOption.RECENTLY_UPDATED -> filtered.sortedWith(
                compareByDescending<Issue> { it.isPinned }.thenByDescending { it.updatedAt }
            )
            IssueSortOption.HIGHEST_PRIORITY -> filtered.sortedWith(
                compareByDescending<Issue> { it.isPinned }.thenByDescending { it.priority.severityLevel }
            )
            IssueSortOption.OLDEST_UNRESOLVED -> filtered.sortedWith(
                compareByDescending<Issue> { it.isPinned }.thenBy { it.createdAt }
            )
        }

        IssuesUiState(
            searchQuery = query,
            filterState = filter,
            sortOption = sort,
            issues = sorted,
            projects = projects,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IssuesUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterChange(newFilter: IssueFilterState) {
        _filterState.value = newFilter
    }

    fun onSortOptionSelected(sortOption: IssueSortOption) {
        _sortOption.value = sortOption
    }

    fun togglePinned(issue: Issue) {
        viewModelScope.launch {
            issueRepository.togglePinned(issue.id, !issue.isPinned)
        }
    }

    fun toggleArchived(issue: Issue) {
        viewModelScope.launch {
            issueRepository.toggleArchived(issue.id, !issue.isArchived)
        }
    }
}
