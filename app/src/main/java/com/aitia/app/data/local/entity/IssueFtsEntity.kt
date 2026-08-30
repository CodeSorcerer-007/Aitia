package com.aitia.app.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = IssueEntity::class)
@Entity(tableName = "issues_fts")
data class IssueFtsEntity(
    val title: String,
    val description: String,
    val screen: String,
    val technicalDetails: String,
    val errorMessage: String,
    val exceptionType: String,
    val suspectedCause: String,
    val solution: String
)
