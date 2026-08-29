package com.aitia.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aitia.app.data.local.dao.AttachmentDao
import com.aitia.app.data.local.dao.ChecklistDao
import com.aitia.app.data.local.dao.EnvironmentDao
import com.aitia.app.data.local.dao.IssueDao
import com.aitia.app.data.local.dao.NoteDao
import com.aitia.app.data.local.dao.ProjectDao
import com.aitia.app.data.local.dao.RelatedIssueDao
import com.aitia.app.data.local.dao.TagDao
import com.aitia.app.data.local.dao.TestingSessionDao
import com.aitia.app.data.local.dao.TimelineDao
import com.aitia.app.data.local.entity.AttachmentEntity
import com.aitia.app.data.local.entity.ChecklistItemEntity
import com.aitia.app.data.local.entity.EnvironmentProfileEntity
import com.aitia.app.data.local.entity.IssueEntity
import com.aitia.app.data.local.entity.IssueNoteEntity
import com.aitia.app.data.local.entity.IssueTagCrossRef
import com.aitia.app.data.local.entity.IssueTimelineEventEntity
import com.aitia.app.data.local.entity.ProjectEntity
import com.aitia.app.data.local.entity.ProjectVersionEntity
import com.aitia.app.data.local.entity.RelatedIssueEntity
import com.aitia.app.data.local.entity.TagEntity
import com.aitia.app.data.local.entity.TestingSessionEntity

@Database(
    entities = [
        ProjectEntity::class,
        ProjectVersionEntity::class,
        EnvironmentProfileEntity::class,
        TestingSessionEntity::class,
        IssueEntity::class,
        IssueNoteEntity::class,
        AttachmentEntity::class,
        TagEntity::class,
        IssueTagCrossRef::class,
        RelatedIssueEntity::class,
        ChecklistItemEntity::class,
        IssueTimelineEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AitiaDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun issueDao(): IssueDao
    abstract fun testingSessionDao(): TestingSessionDao
    abstract fun environmentDao(): EnvironmentDao
    abstract fun noteDao(): NoteDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun tagDao(): TagDao
    abstract fun relatedIssueDao(): RelatedIssueDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun timelineDao(): TimelineDao

    companion object {
        @Volatile
        private var INSTANCE: AitiaDatabase? = null

        fun getDatabase(context: Context): AitiaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AitiaDatabase::class.java,
                    "aitia.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
