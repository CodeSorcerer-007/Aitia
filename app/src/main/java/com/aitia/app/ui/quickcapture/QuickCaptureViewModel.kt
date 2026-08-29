package com.aitia.app.ui.quickcapture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.data.preferences.UserPreferencesRepository
import com.aitia.app.domain.repository.EnvironmentRepository
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.ProjectRepository
import com.aitia.app.domain.repository.TestingSessionRepository
import com.aitia.app.domain.model.EnvironmentProfile
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.TestingSession
import com.aitia.app.domain.similarity.DuplicateDetectionEngine
import com.aitia.app.domain.similarity.DuplicateMatch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant

data class QuickCaptureUiState(
    val title: String = "",
    val type: IssueType = IssueType.BUG,
    val priority: Priority = Priority.MEDIUM,
    val selectedProjectId: Long? = null,
    val selectedEnvironmentId: Long? = null,
    val screenArea: String = "",
    val projects: List<Project> = emptyList(),
    val environments: List<EnvironmentProfile> = emptyList(),
    val activeSession: TestingSession? = null,
    val duplicateMatches: List<DuplicateMatch> = emptyList(),
    val isSaving: Boolean = false
)

class QuickCaptureViewModel(
    private val issueRepository: IssueRepository,
    private val projectRepository: ProjectRepository,
    private val sessionRepository: TestingSessionRepository,
    private val environmentRepository: EnvironmentRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _title = MutableStateFlow("")
    private val _type = MutableStateFlow(IssueType.BUG)
    private val _priority = MutableStateFlow(Priority.MEDIUM)
    private val _selectedProjectId = MutableStateFlow<Long?>(null)
    private val _selectedEnvironmentId = MutableStateFlow<Long?>(null)
    private val _screenArea = MutableStateFlow("")
    private val _duplicateMatches = MutableStateFlow<List<DuplicateMatch>>(emptyList())
    private val _isSaving = MutableStateFlow(false)

    private var duplicateCheckJob: Job? = null
    private var allIssuesCache: List<Issue> = emptyList()

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferences.first()
            _priority.value = prefs.defaultPriority
            _selectedProjectId.value = prefs.defaultProjectId
            if (prefs.quickCaptureDraft.isNotBlank()) {
                _title.value = prefs.quickCaptureDraft
            }
        }

        viewModelScope.launch {
            issueRepository.getActiveIssues().collect {
                allIssuesCache = it
            }
        }
    }

    val uiState: StateFlow<QuickCaptureUiState> = combine(
        _title,
        _type,
        _priority,
        _selectedProjectId,
        _selectedEnvironmentId,
        _screenArea,
        projectRepository.getAllProjects(),
        environmentRepository.getAllEnvironments(),
        sessionRepository.getActiveSession(),
        _duplicateMatches,
        _isSaving
    ) { args ->
        val title = args[0] as String
        val type = args[1] as IssueType
        val priority = args[2] as Priority
        val projId = args[3] as Long?
        val envId = args[4] as Long?
        val screen = args[5] as String
        @Suppress("UNCHECKED_CAST")
        val projects = args[6] as List<Project>
        @Suppress("UNCHECKED_CAST")
        val environments = args[7] as List<EnvironmentProfile>
        val activeSession = args[8] as TestingSession?
        @Suppress("UNCHECKED_CAST")
        val duplicates = args[9] as List<DuplicateMatch>
        val isSaving = args[10] as Boolean

        QuickCaptureUiState(
            title = title,
            type = type,
            priority = priority,
            selectedProjectId = projId,
            selectedEnvironmentId = envId,
            screenArea = screen,
            projects = projects,
            environments = environments,
            activeSession = activeSession,
            duplicateMatches = duplicates,
            isSaving = isSaving
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuickCaptureUiState()
    )

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle

        // Auto save draft to preferences
        viewModelScope.launch {
            preferencesRepository.setQuickCaptureDraft(newTitle)
        }

        // Debounced duplicate detection
        duplicateCheckJob?.cancel()
        duplicateCheckJob = viewModelScope.launch {
            delay(250)
            if (newTitle.length >= 3 && allIssuesCache.isNotEmpty()) {
                val matches = DuplicateDetectionEngine.findDuplicates(newTitle, allIssuesCache)
                _duplicateMatches.value = matches
            } else {
                _duplicateMatches.value = emptyList()
            }
        }
    }

    fun onTypeSelected(type: IssueType) {
        _type.value = type
        if (type == IssueType.CRASH && _priority.value == Priority.LOW) {
            _priority.value = Priority.HIGH
        }
    }

    fun onPrioritySelected(priority: Priority) {
        _priority.value = priority
    }

    fun onProjectSelected(projectId: Long?) {
        _selectedProjectId.value = projectId
    }

    fun onEnvironmentSelected(environmentId: Long?) {
        _selectedEnvironmentId.value = environmentId
    }

    fun onScreenAreaChange(screen: String) {
        _screenArea.value = screen
    }

    fun saveIssue(onComplete: (Long) -> Unit) {
        val titleText = _title.value.trim()
        if (titleText.isBlank()) return

        viewModelScope.launch {
            _isSaving.value = true
            val activeSession = sessionRepository.getActiveSession().first()

            val newIssue = Issue(
                projectId = _selectedProjectId.value,
                title = titleText,
                type = _type.value,
                priority = _priority.value,
                status = IssueStatus.OPEN,
                screen = _screenArea.value.trim(),
                environmentId = _selectedEnvironmentId.value,
                testingSessionId = activeSession?.id,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            val newId = issueRepository.saveIssue(newIssue)
            // Clear draft
            preferencesRepository.setQuickCaptureDraft("")
            _title.value = ""
            _screenArea.value = ""
            _duplicateMatches.value = emptyList()
            _isSaving.value = false
            onComplete(newId)
        }
    }
}
