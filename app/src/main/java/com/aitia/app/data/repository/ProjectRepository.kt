package com.aitia.app.data.repository

import com.aitia.app.data.local.dao.IssueDao
import com.aitia.app.data.local.dao.ProjectDao
import com.aitia.app.data.local.entity.ProjectEntity
import com.aitia.app.data.local.entity.ProjectVersionEntity
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.ProjectVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

import com.aitia.app.domain.repository.ProjectRepository

class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val issueDao: IssueDao
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> {
        return combine(
            projectDao.getAllProjects(),
            issueDao.getAllIssues()
        ) { projects, issues ->
            projects.map { entity ->
                val projectIssues = issues.filter { it.projectId == entity.id }
                val openCount = projectIssues.count { !it.isArchived && !it.status.isResolved }
                val criticalCount = projectIssues.count { !it.isArchived && !it.status.isResolved && it.priority.name == "CRITICAL" }
                val totalCount = projectIssues.size

                Project(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    packageName = entity.packageName,
                    platform = entity.platform,
                    currentVersion = entity.currentVersion,
                    colorHex = entity.colorHex,
                    iconName = entity.iconName,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    openIssueCount = openCount,
                    criticalIssueCount = criticalCount,
                    totalIssueCount = totalCount
                )
            }
        }
    }

    override suspend fun getProjectById(id: Long): Project? {
        val entity = projectDao.getProjectById(id) ?: return null
        return Project(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            packageName = entity.packageName,
            platform = entity.platform,
            currentVersion = entity.currentVersion,
            colorHex = entity.colorHex,
            iconName = entity.iconName,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    override fun observeProjectById(id: Long): Flow<Project?> {
        return projectDao.observeProjectById(id).map { entity ->
            entity?.let {
                Project(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    packageName = it.packageName,
                    platform = it.platform,
                    currentVersion = it.currentVersion,
                    colorHex = it.colorHex,
                    iconName = it.iconName,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    override suspend fun saveProject(project: Project): Long {
        val entity = ProjectEntity(
            id = project.id,
            name = project.name,
            description = project.description,
            packageName = project.packageName,
            platform = project.platform,
            currentVersion = project.currentVersion,
            colorHex = project.colorHex,
            iconName = project.iconName,
            createdAt = if (project.id == 0L) Instant.now() else project.createdAt,
            updatedAt = Instant.now()
        )
        return if (project.id == 0L) {
            projectDao.insertProject(entity)
        } else {
            projectDao.updateProject(entity)
            project.id
        }
    }

    override suspend fun deleteProject(project: Project) {
        val entity = ProjectEntity(
            id = project.id,
            name = project.name,
            description = project.description,
            packageName = project.packageName,
            platform = project.platform,
            currentVersion = project.currentVersion,
            colorHex = project.colorHex,
            iconName = project.iconName,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
        projectDao.deleteProject(entity)
    }

    override fun getVersionsForProject(projectId: Long): Flow<List<ProjectVersion>> {
        return projectDao.getVersionsForProject(projectId).map { list ->
            list.map {
                ProjectVersion(
                    id = it.id,
                    projectId = it.projectId,
                    versionName = it.versionName,
                    buildNumber = it.buildNumber,
                    releaseDate = it.releaseDate,
                    isCurrent = it.isCurrent,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override suspend fun saveVersion(version: ProjectVersion): Long {
        val entity = ProjectVersionEntity(
            id = version.id,
            projectId = version.projectId,
            versionName = version.versionName,
            buildNumber = version.buildNumber,
            releaseDate = version.releaseDate,
            isCurrent = version.isCurrent,
            createdAt = version.createdAt
        )
        return projectDao.insertVersion(entity)
    }

    override suspend fun deleteVersion(version: ProjectVersion) {
        val entity = ProjectVersionEntity(
            id = version.id,
            projectId = version.projectId,
            versionName = version.versionName,
            buildNumber = version.buildNumber,
            releaseDate = version.releaseDate,
            isCurrent = version.isCurrent,
            createdAt = version.createdAt
        )
        projectDao.deleteVersion(entity)
    }
}
