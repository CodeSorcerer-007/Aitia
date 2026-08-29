package com.aitia.app.di

import android.content.Context
import com.aitia.app.data.local.AitiaDatabase
import com.aitia.app.data.preferences.UserPreferencesRepository
import com.aitia.app.data.repository.BackupExportRepository
import com.aitia.app.data.repository.EnvironmentRepositoryImpl
import com.aitia.app.data.repository.IssueRepositoryImpl
import com.aitia.app.data.repository.ProjectRepositoryImpl
import com.aitia.app.data.repository.TestingSessionRepositoryImpl
import com.aitia.app.domain.repository.EnvironmentRepository
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.ProjectRepository
import com.aitia.app.domain.repository.TestingSessionRepository
import com.aitia.app.data.sample.SampleDataSeeder

interface AppContainer {
    val database: AitiaDatabase
    val projectRepository: ProjectRepository
    val issueRepository: IssueRepository
    val sessionRepository: TestingSessionRepository
    val environmentRepository: EnvironmentRepository
    val preferencesRepository: UserPreferencesRepository
    val backupExportRepository: BackupExportRepository
    val sampleDataSeeder: SampleDataSeeder
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val database: AitiaDatabase by lazy {
        AitiaDatabase.getDatabase(context)
    }

    override val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl(
            projectDao = database.projectDao(),
            issueDao = database.issueDao()
        )
    }

    override val environmentRepository: EnvironmentRepository by lazy {
        EnvironmentRepositoryImpl(
            environmentDao = database.environmentDao()
        )
    }

    override val sessionRepository: TestingSessionRepository by lazy {
        TestingSessionRepositoryImpl(
            sessionDao = database.testingSessionDao(),
            projectDao = database.projectDao(),
            environmentDao = database.environmentDao(),
            issueDao = database.issueDao()
        )
    }

    override val issueRepository: IssueRepository by lazy {
        IssueRepositoryImpl(
            issueDao = database.issueDao(),
            projectDao = database.projectDao(),
            sessionDao = database.testingSessionDao(),
            environmentDao = database.environmentDao(),
            noteDao = database.noteDao(),
            attachmentDao = database.attachmentDao(),
            tagDao = database.tagDao(),
            relatedDao = database.relatedIssueDao(),
            checklistDao = database.checklistDao(),
            timelineDao = database.timelineDao()
        )
    }

    override val backupExportRepository: BackupExportRepository by lazy {
        BackupExportRepository(
            context = context,
            database = database,
            issueRepository = issueRepository,
            projectRepository = projectRepository
        )
    }

    override val sampleDataSeeder: SampleDataSeeder by lazy {
        SampleDataSeeder(database)
    }
}
