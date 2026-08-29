package com.aitia.app.domain.repository

import com.aitia.app.domain.model.EnvironmentProfile
import kotlinx.coroutines.flow.Flow

interface EnvironmentRepository {
    fun getAllEnvironments(): Flow<List<EnvironmentProfile>>
    suspend fun getEnvironmentById(id: Long): EnvironmentProfile?
    suspend fun saveEnvironment(env: EnvironmentProfile): Long
    suspend fun deleteEnvironment(env: EnvironmentProfile)
}
