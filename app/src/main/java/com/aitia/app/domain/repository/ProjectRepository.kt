package com.aitia.app.domain.repository

import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.ProjectVersion
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    suspend fun getProjectById(id: Long): Project?
    fun observeProjectById(id: Long): Flow<Project?>
    suspend fun saveProject(project: Project): Long
    suspend fun deleteProject(project: Project)
    fun getVersionsForProject(projectId: Long): Flow<List<ProjectVersion>>
    suspend fun saveVersion(version: ProjectVersion): Long
    suspend fun deleteVersion(version: ProjectVersion)
}
