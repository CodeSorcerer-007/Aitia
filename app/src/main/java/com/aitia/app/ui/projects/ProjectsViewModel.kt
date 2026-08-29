package com.aitia.app.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.data.repository.EnvironmentRepository
import com.aitia.app.data.repository.ProjectRepository
import com.aitia.app.data.repository.TestingSessionRepository
import com.aitia.app.domain.model.EnvironmentProfile
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.ProjectVersion
import com.aitia.app.domain.model.TestingSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val environments: List<EnvironmentProfile> = emptyList(),
    val sessions: List<TestingSession> = emptyList(),
    val isLoading: Boolean = false
)

class ProjectsViewModel(
    private val projectRepository: ProjectRepository,
    private val environmentRepository: EnvironmentRepository,
    private val sessionRepository: TestingSessionRepository
) : ViewModel() {

    val uiState: StateFlow<ProjectsUiState> = combine(
        projectRepository.getAllProjects(),
        environmentRepository.getAllEnvironments(),
        sessionRepository.getAllSessions()
    ) { projects, envs, sessions ->
        ProjectsUiState(
            projects = projects,
            environments = envs,
            sessions = sessions,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProjectsUiState(isLoading = true)
    )

    fun createProject(name: String, description: String, packageName: String, currentVersion: String, colorHex: String) {
        viewModelScope.launch {
            val proj = Project(
                name = name.trim(),
                description = description.trim(),
                packageName = packageName.trim(),
                currentVersion = currentVersion.trim().ifBlank { "1.0.0" },
                colorHex = colorHex
            )
            projectRepository.saveProject(proj)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            projectRepository.deleteProject(project)
        }
    }

    fun createEnvironment(name: String, device: String, androidVersion: String, appVersion: String, projectId: Long?) {
        viewModelScope.launch {
            val env = EnvironmentProfile(
                name = name.trim(),
                device = device.trim().ifBlank { "Pixel 8" },
                androidVersion = androidVersion.trim().ifBlank { "Android 15" },
                appVersion = appVersion.trim().ifBlank { "1.0.0" },
                projectId = projectId
            )
            environmentRepository.saveEnvironment(env)
        }
    }

    fun deleteEnvironment(env: EnvironmentProfile) {
        viewModelScope.launch {
            environmentRepository.deleteEnvironment(env)
        }
    }

    fun startTestingSession(name: String, projectId: Long?, environmentId: Long?) {
        viewModelScope.launch {
            sessionRepository.startSession(projectId, name, environmentId)
        }
    }
}
