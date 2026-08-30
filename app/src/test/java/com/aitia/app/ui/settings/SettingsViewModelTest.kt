package com.aitia.app.ui.settings

import app.cash.turbine.test
import com.aitia.app.data.preferences.UserPreferencesRepository
import com.aitia.app.data.repository.BackupExportRepository
import com.aitia.app.data.repository.SettingsRepository
import com.aitia.app.data.sample.SampleDataSeeder
import com.aitia.app.domain.model.AppThemeMode
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.UserPreferences
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.ProjectRepository
import com.aitia.app.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var preferencesRepository: UserPreferencesRepository
    private lateinit var backupExportRepository: BackupExportRepository
    private lateinit var sampleDataSeeder: SampleDataSeeder
    private lateinit var issueRepository: IssueRepository
    private lateinit var projectRepository: ProjectRepository
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        preferencesRepository = mockk(relaxed = true)
        backupExportRepository = mockk(relaxed = true)
        sampleDataSeeder = mockk(relaxed = true)
        issueRepository = mockk(relaxed = true)
        projectRepository = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)

        every { preferencesRepository.userPreferences } returns flowOf(UserPreferences())
        every { settingsRepository.geminiApiKey } returns flowOf("test-gemini-key")
        every { settingsRepository.githubPat } returns flowOf("test-github-key")
        every { settingsRepository.defaultGithubRepo } returns flowOf("test/repo")

        viewModel = SettingsViewModel(
            preferencesRepository,
            backupExportRepository,
            sampleDataSeeder,
            issueRepository,
            projectRepository,
            settingsRepository
        )
    }

    @Test
    fun `uiState initially loads with preferences and keys`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            
            assertEquals("test-gemini-key", initialState.geminiApiKey)
            assertEquals("test-github-key", initialState.githubPat)
            assertEquals("test/repo", initialState.defaultRepo)
            assertEquals(UserPreferences(), initialState.preferences)
        }
    }

    @Test
    fun `updateThemeMode updates repository`() = runTest {
        viewModel.updateThemeMode(AppThemeMode.DARK)
        coVerify { preferencesRepository.setThemeMode(AppThemeMode.DARK) }
    }

    @Test
    fun `saveGeminiApiKey calls settings repository`() = runTest {
        viewModel.saveGeminiApiKey("new-gemini")
        coVerify { settingsRepository.saveGeminiApiKey("new-gemini") }
    }

    @Test
    fun `saveGithubPat calls settings repository`() = runTest {
        viewModel.saveGithubPat("new-pat")
        coVerify { settingsRepository.saveGithubPat("new-pat") }
    }
}
