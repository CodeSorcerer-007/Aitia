package com.aitia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aitia.app.data.local.entity.IssueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {

    @Query("SELECT * FROM issues WHERE isArchived = 0 ORDER BY isPinned DESC, createdAt DESC")
    fun getActiveIssues(): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedIssues(): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues ORDER BY isPinned DESC, createdAt DESC")
    fun getAllIssues(): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE isPinned = 1 AND isArchived = 0 ORDER BY createdAt DESC")
    fun getPinnedIssues(): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE isArchived = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentIssues(limit: Int): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE id = :id")
    suspend fun getIssueById(id: Long): IssueEntity?

    @Query("SELECT * FROM issues WHERE id = :id")
    fun observeIssueById(id: Long): Flow<IssueEntity?>

    @Query("SELECT * FROM issues WHERE projectId = :projectId AND isArchived = 0 ORDER BY isPinned DESC, createdAt DESC")
    fun getIssuesForProject(projectId: Long): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE testingSessionId = :sessionId ORDER BY createdAt DESC")
    fun getIssuesForSession(sessionId: Long): Flow<List<IssueEntity>>

    @Query("""
        SELECT * FROM issues 
        WHERE isArchived = 0 AND (
            title LIKE '%' || :query || '%' OR 
            description LIKE '%' || :query || '%' OR 
            screen LIKE '%' || :query || '%' OR 
            technicalDetails LIKE '%' || :query || '%' OR 
            errorMessage LIKE '%' || :query || '%' OR 
            exceptionType LIKE '%' || :query || '%' OR 
            suspectedCause LIKE '%' || :query || '%' OR 
            solution LIKE '%' || :query || '%'
        )
        ORDER BY isPinned DESC, createdAt DESC
    """)
    fun searchIssues(query: String): Flow<List<IssueEntity>>

    // Statistics counts
    @Query("SELECT COUNT(*) FROM issues WHERE isArchived = 0 AND status NOT IN ('FIXED', 'VERIFIED', 'CLOSED')")
    fun getOpenIssueCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM issues WHERE isArchived = 0 AND priority IN ('HIGH', 'CRITICAL') AND status NOT IN ('FIXED', 'VERIFIED', 'CLOSED')")
    fun getCriticalHighIssueCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM issues WHERE type = 'CRASH'")
    fun getCrashCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM issues WHERE status IN ('FIXED', 'VERIFIED')")
    fun getFixedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM issues")
    fun getTotalIssueCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: IssueEntity): Long

    @Update
    suspend fun updateIssue(issue: IssueEntity)

    @Delete
    suspend fun deleteIssue(issue: IssueEntity)

    @Query("UPDATE issues SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePinned(id: Long, isPinned: Boolean, updatedAt: Long)

    @Query("UPDATE issues SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateArchived(id: Long, isArchived: Boolean, updatedAt: Long)

    @Query("DELETE FROM issues")
    suspend fun clearAll()
}
