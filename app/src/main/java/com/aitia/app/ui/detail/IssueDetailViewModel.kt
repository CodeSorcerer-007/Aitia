package com.aitia.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.data.repository.BackupExportRepository
import com.aitia.app.data.repository.SettingsRepository
import com.aitia.app.domain.repository.EnvironmentRepository
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.ProjectRepository
import com.aitia.app.domain.model.Attachment
import com.aitia.app.domain.model.ChecklistItem
import com.aitia.app.domain.model.EnvironmentProfile
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueNote
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.RelatedIssue
import com.aitia.app.domain.model.RelationshipType
import com.aitia.app.domain.model.TimelineEvent
import com.aitia.app.domain.parser.StackTraceParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

data class IssueDetailUiState(
    val issue: Issue? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class IssueDetailViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val projectRepository: ProjectRepository,
    private val environmentRepository: EnvironmentRepository,
    private val backupExportRepository: BackupExportRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _issueId = MutableStateFlow<Long?>(null)
    private val _isEditing = MutableStateFlow(false)

    fun setIssueId(id: Long) {
        _issueId.value = id
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<IssueDetailUiState> = _issueId.flatMapLatest { id ->
        if (id == null) {
            flowOf(IssueDetailUiState(isLoading = true))
        } else {
            combine(
                issueRepository.observeIssueById(id),
                _isEditing
            ) { issue, isEditing ->
                IssueDetailUiState(
                    issue = issue,
                    isEditing = isEditing,
                    isLoading = false
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IssueDetailUiState()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allIssuesFlow: StateFlow<List<Issue>> = _issueId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else issueRepository.getAllIssues().map { list -> list.filter { it.id != id } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val projectsFlow: StateFlow<List<Project>> = projectRepository.getAllProjects().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val environmentsFlow: StateFlow<List<EnvironmentProfile>> = environmentRepository.getAllEnvironments().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getNotesFlow(issueId: Long) = issueRepository.getNotesForIssue(issueId)
    fun getAttachmentsFlow(issueId: Long) = issueRepository.getAttachmentsForIssue(issueId)
    fun getChecklistFlow(issueId: Long) = issueRepository.getChecklistForIssue(issueId)
    fun getRelatedFlow(issueId: Long) = issueRepository.getRelatedIssues(issueId)
    fun getTimelineFlow(issueId: Long) = issueRepository.getTimelineForIssue(issueId)
    
    fun getGeminiApiKey(): String = settingsRepository.getGeminiApiKey()
    fun getGithubPat(): String = settingsRepository.getGithubPat()
    fun getDefaultRepo(): String = settingsRepository.getDefaultRepo()

    fun updateStatus(newStatus: IssueStatus) {
        val id = _issueId.value ?: return
        viewModelScope.launch {
            issueRepository.updateStatus(id, newStatus)
        }
    }

    fun updateIssueField(updateBlock: (Issue) -> Issue) {
        val current = uiState.value.issue ?: return
        val updated = updateBlock(current)
        viewModelScope.launch {
            issueRepository.saveIssue(updated)
        }
    }

    fun togglePinned() {
        val current = uiState.value.issue ?: return
        viewModelScope.launch {
            issueRepository.togglePinned(current.id, !current.isPinned)
        }
    }

    fun toggleArchived() {
        val current = uiState.value.issue ?: return
        viewModelScope.launch {
            issueRepository.toggleArchived(current.id, !current.isArchived)
        }
    }

    fun deleteIssue(onComplete: () -> Unit) {
        val current = uiState.value.issue ?: return
        viewModelScope.launch {
            issueRepository.deleteIssue(current)
            onComplete()
        }
    }

    fun addNote(text: String) {
        val id = _issueId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            issueRepository.addNote(id, text)
        }
    }

    fun deleteNote(note: IssueNote) {
        viewModelScope.launch {
            issueRepository.deleteNote(note.id, note.issueId, note.text)
        }
    }

    fun addChecklistItem(text: String) {
        val id = _issueId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            issueRepository.addChecklistItem(id, text)
        }
    }

    fun toggleChecklistItem(item: ChecklistItem, isCompleted: Boolean) {
        viewModelScope.launch {
            issueRepository.toggleChecklistItem(item.id, isCompleted)
        }
    }

    fun deleteChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            issueRepository.deleteChecklistItem(item)
        }
    }

    fun addAttachment(filename: String, uriPath: String, mimeType: String, sizeBytes: Long) {
        val id = _issueId.value ?: return
        viewModelScope.launch {
            issueRepository.addAttachment(id, uriPath, filename, mimeType, sizeBytes)
        }
    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            issueRepository.deleteAttachment(attachment)
        }
    }

    fun getTagsFlow(issueId: Long) = issueRepository.getTagsForIssue(issueId)
    fun getAllTagsFlow() = issueRepository.getAllTags()

    fun addTag(tagName: String) {
        val id = _issueId.value ?: return
        if (tagName.isBlank()) return
        viewModelScope.launch {
            issueRepository.addTagToIssue(id, tagName)
        }
    }

    fun removeTag(tagId: Long) {
        val id = _issueId.value ?: return
        viewModelScope.launch {
            issueRepository.removeTagFromIssue(id, tagId)
        }
    }

    fun linkRelatedIssue(targetIssueId: Long, relationshipType: RelationshipType) {
        val id = _issueId.value ?: return
        viewModelScope.launch {
            issueRepository.addRelatedIssue(id, targetIssueId, relationshipType)
        }
    }

    fun parseLogsAndAutoPopulate() {
        val current = uiState.value.issue ?: return
        if (current.technicalDetails.isBlank()) return
        viewModelScope.launch {
            val parsed = StackTraceParser.parse(current.technicalDetails)
            if (parsed.isParsed) {
                val updated = current.copy(
                    exceptionType = parsed.exceptionType ?: current.exceptionType,
                    errorMessage = parsed.errorMessage ?: current.errorMessage,
                    sourceFile = parsed.sourceFile ?: current.sourceFile,
                    sourceLine = parsed.sourceLine ?: current.sourceLine
                )
                issueRepository.saveIssue(updated)
            }
        }
    }

    fun generateMarkdownReport(): String {
        val current = uiState.value.issue ?: return ""
        return backupExportRepository.generateMarkdownReport(listOf(current), current.projectName ?: "Aitia Bug Report")
    }

    fun harvestLogcatToTechnicalDetails() {
        val current = uiState.value.issue ?: return
        viewModelScope.launch {
            val logs = com.aitia.app.util.LogcatHarvester.harvestRecentLogs(100)
            val updatedTech = if (current.technicalDetails.isBlank()) logs else "${current.technicalDetails}\n\n// --- Logcat Dump ---\n$logs"
            val parsed = StackTraceParser.parse(logs)
            val updated = current.copy(
                technicalDetails = updatedTech,
                exceptionType = parsed.exceptionType ?: current.exceptionType,
                errorMessage = parsed.errorMessage ?: current.errorMessage,
                sourceFile = parsed.sourceFile ?: current.sourceFile,
                sourceLine = parsed.sourceLine ?: current.sourceLine
            )
            issueRepository.saveIssue(updated)
        }
    }

    fun appendDeviceVitals(context: android.content.Context) {
        val current = uiState.value.issue ?: return
        val vitals = com.aitia.app.util.DeviceVitalsHarvester.capture(context)
        val vitalsMd = com.aitia.app.util.DeviceVitalsHarvester.formatMarkdown(vitals)
        val updatedTech = if (current.technicalDetails.isBlank()) vitalsMd else "$vitalsMd\n\n${current.technicalDetails}"
        viewModelScope.launch {
            issueRepository.saveIssue(current.copy(technicalDetails = updatedTech))
        }
    }
}
