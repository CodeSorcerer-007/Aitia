package com.aitia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aitia.app.data.local.entity.EnvironmentProfileEntity
import com.aitia.app.data.local.entity.TestingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestingSessionDao {

    @Query("SELECT * FROM testing_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<TestingSessionEntity>>

    @Query("SELECT * FROM testing_sessions WHERE endedAt IS NULL LIMIT 1")
    fun getActiveSession(): Flow<TestingSessionEntity?>

    @Query("SELECT * FROM testing_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): TestingSessionEntity?

    @Query("SELECT * FROM testing_sessions WHERE id = :id")
    fun observeSessionById(id: Long): Flow<TestingSessionEntity?>

    @Query("SELECT * FROM testing_sessions WHERE projectId = :projectId ORDER BY startedAt DESC")
    fun getSessionsForProject(projectId: Long): Flow<List<TestingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TestingSessionEntity): Long

    @Update
    suspend fun updateSession(session: TestingSessionEntity)

    @Delete
    suspend fun deleteSession(session: TestingSessionEntity)

    @Query("DELETE FROM testing_sessions")
    suspend fun clearAll()
}

@Dao
interface EnvironmentDao {

    @Query("SELECT * FROM environment_profiles ORDER BY createdAt DESC")
    fun getAllEnvironments(): Flow<List<EnvironmentProfileEntity>>

    @Query("SELECT * FROM environment_profiles WHERE id = :id")
    suspend fun getEnvironmentById(id: Long): EnvironmentProfileEntity?

    @Query("SELECT * FROM environment_profiles WHERE projectId = :projectId OR projectId IS NULL ORDER BY createdAt DESC")
    fun getEnvironmentsForProject(projectId: Long?): Flow<List<EnvironmentProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvironment(env: EnvironmentProfileEntity): Long

    @Update
    suspend fun updateEnvironment(env: EnvironmentProfileEntity)

    @Delete
    suspend fun deleteEnvironment(env: EnvironmentProfileEntity)

    @Query("DELETE FROM environment_profiles")
    suspend fun clearAll()
}
