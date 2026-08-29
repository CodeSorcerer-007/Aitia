package com.aitia.app.domain.repository

import com.aitia.app.domain.model.TestingSession
import kotlinx.coroutines.flow.Flow

interface TestingSessionRepository {
    fun getAllSessions(): Flow<List<TestingSession>>
    fun getActiveSession(): Flow<TestingSession?>
    suspend fun getSessionById(id: Long): TestingSession?
    fun observeSessionById(id: Long): Flow<TestingSession?>
    suspend fun startSession(projectId: Long?, name: String, environmentId: Long?): Long
    suspend fun stopSession(id: Long, notes: String = "")
    suspend fun deleteSession(session: TestingSession)
}
