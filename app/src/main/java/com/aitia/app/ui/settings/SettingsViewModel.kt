package com.aitia.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitia.app.data.preferences.UserPreferencesRepository
import com.aitia.app.data.repository.BackupExportRepository
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.ProjectRepository
import com.aitia.app.data.sample.SampleDataSeeder
import com.aitia.app.domain.model.AppThemeMode
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val isSeeding: Boolean = false,
    val isExporting: Boolean = false,
    val statusMessage: String? = null
)

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val backupExportRepository: BackupExportRepository,
    private val sampleDataSeeder: SampleDataSeeder,
    private val issueRepository: IssueRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _isSeeding = MutableStateFlow(false)
    private val _isExporting = MutableStateFlow(false)
    private val _statusMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = kotlinx.coroutines.flow.combine(
        preferencesRepository.userPreferences,
        _isSeeding,
        _isExporting,
        _statusMessage
    ) { prefs, isSeeding, isExporting, statusMsg ->
        SettingsUiState(
            preferences = prefs,
            isSeeding = isSeeding,
            isExporting = isExporting,
            statusMessage = statusMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(themeMode)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setHapticFeedback(enabled)
        }
    }

    fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setReducedMotion(enabled)
        }
    }

    fun setDefaultPriority(priority: Priority) {
        viewModelScope.launch {
            preferencesRepository.setDefaultPriority(priority)
        }
    }

    fun setAppLock(enabled: Boolean, pin: String = "", biometric: Boolean = false) {
        viewModelScope.launch {
            preferencesRepository.setAppLock(enabled, pin, biometric)
        }
    }

    fun setShakeToReport(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShakeToReport(enabled)
        }
    }

    fun seedSampleData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSeeding.value = true
            sampleDataSeeder.seedSampleData()
            _isSeeding.value = false
            _statusMessage.value = "Realistic developer sample data populated successfully!"
            onSuccess()
        }
    }

    fun clearAllData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sampleDataSeeder.clearAllData()
            _statusMessage.value = "All data cleared successfully."
            onSuccess()
        }
    }

    fun exportBackupJson(onReady: (File) -> Unit) {
        viewModelScope.launch {
            _isExporting.value = true
            val json = backupExportRepository.exportToJson()
            val file = backupExportRepository.writeExportFile(json, "aitia_backup_${System.currentTimeMillis()}.json")
            _isExporting.value = false
            onReady(file)
        }
    }

    fun exportMarkdown(onReady: (File) -> Unit) {
        viewModelScope.launch {
            _isExporting.value = true
            val issues = issueRepository.getActiveIssues().first()
            val md = backupExportRepository.generateMarkdownReport(issues, "Aitia Bug Report")
            val file = backupExportRepository.writeExportFile(md, "aitia_report_${System.currentTimeMillis()}.md")
            _isExporting.value = false
            onReady(file)
        }
    }

    fun exportCsv(onReady: (File) -> Unit) {
        viewModelScope.launch {
            _isExporting.value = true
            val issues = issueRepository.getActiveIssues().first()
            val csv = backupExportRepository.generateCsvExport(issues)
            val file = backupExportRepository.writeExportFile(csv, "aitia_issues_${System.currentTimeMillis()}.csv")
            _isExporting.value = false
            onReady(file)
        }
    }

    fun importBackupJson(jsonContent: String, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val result = backupExportRepository.importFromJson(jsonContent)
            if (result.isSuccess) {
                _statusMessage.value = "Imported ${result.getOrNull()} items successfully!"
                onSuccess(result.getOrNull() ?: 0)
            } else {
                _statusMessage.value = "Failed to import backup: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
