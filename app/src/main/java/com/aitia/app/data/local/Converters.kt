package com.aitia.app.data.local

import androidx.room.TypeConverter
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.RelationshipType
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilli()
    }

    @TypeConverter
    fun fromIssueType(value: IssueType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toIssueType(value: String?): IssueType {
        return IssueType.fromString(value)
    }

    @TypeConverter
    fun fromIssueStatus(value: IssueStatus?): String? {
        return value?.name
    }

    @TypeConverter
    fun toIssueStatus(value: String?): IssueStatus {
        return IssueStatus.fromString(value)
    }

    @TypeConverter
    fun fromPriority(value: Priority?): String? {
        return value?.name
    }

    @TypeConverter
    fun toPriority(value: String?): Priority {
        return Priority.fromString(value)
    }

    @TypeConverter
    fun fromRelationshipType(value: RelationshipType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRelationshipType(value: String?): RelationshipType {
        return RelationshipType.fromString(value)
    }
}
