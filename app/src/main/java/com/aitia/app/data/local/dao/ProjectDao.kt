package com.aitia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aitia.app.data.local.entity.ProjectEntity
import com.aitia.app.data.local.entity.ProjectVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Query("SELECT * FROM projects WHERE id = :id")
    fun observeProjectById(id: Long): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // Project Versions
    @Query("SELECT * FROM project_versions WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getVersionsForProject(projectId: Long): Flow<List<ProjectVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: ProjectVersionEntity): Long

    @Delete
    suspend fun deleteVersion(version: ProjectVersionEntity)

    @Query("DELETE FROM projects")
    suspend fun clearAll()
}
