package com.aitia.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aitia.app.ui.analytics.AnalyticsViewModel
import com.aitia.app.ui.detail.IssueDetailViewModel
import com.aitia.app.ui.home.HomeViewModel
import com.aitia.app.ui.issues.IssuesViewModel
import com.aitia.app.ui.projects.ProjectsViewModel
import com.aitia.app.ui.quickcapture.QuickCaptureViewModel
import com.aitia.app.ui.settings.SettingsViewModel

class ViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    issueRepository = appContainer.issueRepository,
                    projectRepository = appContainer.projectRepository,
                    sessionRepository = appContainer.sessionRepository,
                    preferencesRepository = appContainer.preferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(QuickCaptureViewModel::class.java) -> {
                QuickCaptureViewModel(
                    issueRepository = appContainer.issueRepository,
                    projectRepository = appContainer.projectRepository,
                    sessionRepository = appContainer.sessionRepository,
                    environmentRepository = appContainer.environmentRepository,
                    preferencesRepository = appContainer.preferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(IssuesViewModel::class.java) -> {
                IssuesViewModel(
                    issueRepository = appContainer.issueRepository,
                    projectRepository = appContainer.projectRepository
                ) as T
            }
            modelClass.isAssignableFrom(IssueDetailViewModel::class.java) -> {
                IssueDetailViewModel(
                    issueRepository = appContainer.issueRepository,
                    projectRepository = appContainer.projectRepository,
                    environmentRepository = appContainer.environmentRepository,
                    backupExportRepository = appContainer.backupExportRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProjectsViewModel::class.java) -> {
                ProjectsViewModel(
                    projectRepository = appContainer.projectRepository,
                    environmentRepository = appContainer.environmentRepository,
                    sessionRepository = appContainer.sessionRepository
                ) as T
            }
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) -> {
                AnalyticsViewModel(
                    issueRepository = appContainer.issueRepository,
                    sessionRepository = appContainer.sessionRepository
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    preferencesRepository = appContainer.preferencesRepository,
                    backupExportRepository = appContainer.backupExportRepository,
                    sampleDataSeeder = appContainer.sampleDataSeeder,
                    issueRepository = appContainer.issueRepository,
                    projectRepository = appContainer.projectRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
