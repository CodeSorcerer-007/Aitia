package com.aitia.app.di

import android.content.Context
import com.aitia.app.data.local.AitiaDatabase
import com.aitia.app.data.preferences.UserPreferencesRepository
import com.aitia.app.data.repository.BackupExportRepository
import com.aitia.app.data.repository.EnvironmentRepositoryImpl
import com.aitia.app.data.repository.IssueRepositoryImpl
import com.aitia.app.data.repository.ProjectRepositoryImpl
import com.aitia.app.data.repository.SettingsRepository
import com.aitia.app.data.repository.TestingSessionRepositoryImpl
import com.aitia.app.domain.repository.EnvironmentRepository
import com.aitia.app.domain.repository.IssueRepository
import com.aitia.app.domain.repository.ProjectRepository
import com.aitia.app.domain.repository.TestingSessionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        impl: ProjectRepositoryImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindIssueRepository(
        impl: IssueRepositoryImpl
    ): IssueRepository

    @Binds
    @Singleton
    abstract fun bindEnvironmentRepository(
        impl: EnvironmentRepositoryImpl
    ): EnvironmentRepository

    @Binds
    @Singleton
    abstract fun bindTestingSessionRepository(
        impl: TestingSessionRepositoryImpl
    ): TestingSessionRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvidesModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideBackupExportRepository(
        @ApplicationContext context: Context,
        database: AitiaDatabase,
        issueRepository: IssueRepository,
        projectRepository: ProjectRepository
    ): BackupExportRepository {
        return BackupExportRepository(context, database, issueRepository, projectRepository)
    }
}
