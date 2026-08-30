package com.aitia.app.di

import android.content.Context
import com.aitia.app.data.local.AitiaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AitiaDatabase {
        return AitiaDatabase.getDatabase(context)
    }

    @Provides
    fun provideProjectDao(database: AitiaDatabase) = database.projectDao()
    
    @Provides
    fun provideIssueDao(database: AitiaDatabase) = database.issueDao()
    
    @Provides
    fun provideEnvironmentDao(database: AitiaDatabase) = database.environmentDao()
    
    @Provides
    fun provideTestingSessionDao(database: AitiaDatabase) = database.testingSessionDao()
    
    @Provides
    fun provideNoteDao(database: AitiaDatabase) = database.noteDao()
    
    @Provides
    fun provideAttachmentDao(database: AitiaDatabase) = database.attachmentDao()
    
    @Provides
    fun provideTagDao(database: AitiaDatabase) = database.tagDao()
    
    @Provides
    fun provideRelatedIssueDao(database: AitiaDatabase) = database.relatedIssueDao()
    
    @Provides
    fun provideChecklistDao(database: AitiaDatabase) = database.checklistDao()
    
    @Provides
    fun provideTimelineDao(database: AitiaDatabase) = database.timelineDao()
}
