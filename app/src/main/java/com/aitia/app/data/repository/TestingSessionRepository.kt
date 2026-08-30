package com.aitia.app.data.repository

import com.aitia.app.data.local.dao.EnvironmentDao
import com.aitia.app.data.local.dao.IssueDao
import com.aitia.app.data.local.dao.ProjectDao
import com.aitia.app.data.local.dao.TestingSessionDao
import com.aitia.app.data.local.entity.EnvironmentProfileEntity
import com.aitia.app.data.local.entity.TestingSessionEntity
import com.aitia.app.domain.model.EnvironmentProfile
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.TestingSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant

import com.aitia.app.domain.repository.TestingSessionRepository
import com.aitia.app.domain.repository.EnvironmentRepository
import javax.inject.Inject

class TestingSessionRepositoryImpl @Inject constructor(
    private val sessionDao: TestingSessionDao,
    private val projectDao: ProjectDao,
    private val environmentDao: EnvironmentDao,
    private val issueDao: IssueDao
) : TestingSessionRepository {

    override fun getAllSessions(): Flow<List<TestingSession>> {
        return combine(
            sessionDao.getAllSessions(),
            projectDao.getAllProjects(),
            environmentDao.getAllEnvironments(),
            issueDao.getAllIssues()
        ) { sessions, projects, environments, issues ->
            sessions.map { sessionEntity ->
                val project = projects.firstOrNull { it.id == sessionEntity.projectId }
                val env = environments.firstOrNull { it.id == sessionEntity.environmentId }
                val sessionIssues = issues.filter { it.testingSessionId == sessionEntity.id }

                TestingSession(
                    id = sessionEntity.id,
                    projectId = sessionEntity.projectId,
                    projectName = project?.name,
                    name = sessionEntity.name,
                    startedAt = sessionEntity.startedAt,
                    endedAt = sessionEntity.endedAt,
                    environmentId = sessionEntity.environmentId,
                    environmentName = env?.name,
                    notes = sessionEntity.notes,
                    totalIssuesFound = sessionIssues.size,
                    crashesFound = sessionIssues.count { it.type == IssueType.CRASH },
                    bugsFound = sessionIssues.count { it.type == IssueType.BUG }
                )
            }
        }
    }

    override fun getActiveSession(): Flow<TestingSession?> {
        return combine(
            sessionDao.getActiveSession(),
            projectDao.getAllProjects(),
            environmentDao.getAllEnvironments(),
            issueDao.getAllIssues()
        ) { activeEntity, projects, environments, issues ->
            activeEntity?.let { entity ->
                val project = projects.firstOrNull { it.id == entity.projectId }
                val env = environments.firstOrNull { it.id == entity.environmentId }
                val sessionIssues = issues.filter { it.testingSessionId == entity.id }

                TestingSession(
                    id = entity.id,
                    projectId = entity.projectId,
                    projectName = project?.name,
                    name = entity.name,
                    startedAt = entity.startedAt,
                    endedAt = entity.endedAt,
                    environmentId = entity.environmentId,
                    environmentName = env?.name,
                    notes = entity.notes,
                    totalIssuesFound = sessionIssues.size,
                    crashesFound = sessionIssues.count { it.type == IssueType.CRASH },
                    bugsFound = sessionIssues.count { it.type == IssueType.BUG }
                )
            }
        }
    }

    override suspend fun getSessionById(id: Long): TestingSession? {
        val entity = sessionDao.getSessionById(id) ?: return null
        val project = entity.projectId?.let { projectDao.getProjectById(it) }
        val env = entity.environmentId?.let { environmentDao.getEnvironmentById(it) }

        return TestingSession(
            id = entity.id,
            projectId = entity.projectId,
            projectName = project?.name,
            name = entity.name,
            startedAt = entity.startedAt,
            endedAt = entity.endedAt,
            environmentId = entity.environmentId,
            environmentName = env?.name,
            notes = entity.notes
        )
    }

    override fun observeSessionById(id: Long): Flow<TestingSession?> {
        return combine(
            sessionDao.observeSessionById(id),
            projectDao.getAllProjects(),
            environmentDao.getAllEnvironments(),
            issueDao.getAllIssues()
        ) { entity, projects, environments, issues ->
            entity?.let {
                val project = projects.firstOrNull { p -> p.id == it.projectId }
                val env = environments.firstOrNull { e -> e.id == it.environmentId }
                val sessionIssues = issues.filter { i -> i.testingSessionId == it.id }

                TestingSession(
                    id = it.id,
                    projectId = it.projectId,
                    projectName = project?.name,
                    name = it.name,
                    startedAt = it.startedAt,
                    endedAt = it.endedAt,
                    environmentId = it.environmentId,
                    environmentName = env?.name,
                    notes = it.notes,
                    totalIssuesFound = sessionIssues.size,
                    crashesFound = sessionIssues.count { i -> i.type == IssueType.CRASH },
                    bugsFound = sessionIssues.count { i -> i.type == IssueType.BUG }
                )
            }
        }
    }

    override suspend fun startSession(projectId: Long?, name: String, environmentId: Long?): Long {
        val entity = TestingSessionEntity(
            projectId = projectId,
            name = name.ifBlank { "Testing Session — ${Instant.now()}" },
            startedAt = Instant.now(),
            endedAt = null,
            environmentId = environmentId,
            notes = ""
        )
        return sessionDao.insertSession(entity)
    }

    override suspend fun stopSession(id: Long, notes: String) {
        val current = sessionDao.getSessionById(id) ?: return
        val updated = current.copy(
            endedAt = Instant.now(),
            notes = if (notes.isNotBlank()) notes else current.notes
        )
        sessionDao.updateSession(updated)
    }

    override suspend fun deleteSession(session: TestingSession) {
        val entity = TestingSessionEntity(
            id = session.id,
            projectId = session.projectId,
            name = session.name,
            startedAt = session.startedAt,
            endedAt = session.endedAt,
            environmentId = session.environmentId,
            notes = session.notes
        )
        sessionDao.deleteSession(entity)
    }
}



class EnvironmentRepositoryImpl @Inject constructor(
    private val environmentDao: EnvironmentDao
) : EnvironmentRepository {

    override fun getAllEnvironments(): Flow<List<EnvironmentProfile>> {
        return environmentDao.getAllEnvironments().map { list ->
            list.map {
                EnvironmentProfile(
                    id = it.id,
                    projectId = it.projectId,
                    name = it.name,
                    device = it.device,
                    androidVersion = it.androidVersion,
                    appVersion = it.appVersion,
                    buildNumber = it.buildNumber,
                    notes = it.notes,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override suspend fun getEnvironmentById(id: Long): EnvironmentProfile? {
        val it = environmentDao.getEnvironmentById(id) ?: return null
        return EnvironmentProfile(
            id = it.id,
            projectId = it.projectId,
            name = it.name,
            device = it.device,
            androidVersion = it.androidVersion,
            appVersion = it.appVersion,
            buildNumber = it.buildNumber,
            notes = it.notes,
            createdAt = it.createdAt
        )
    }

    override suspend fun saveEnvironment(env: EnvironmentProfile): Long {
        val entity = EnvironmentProfileEntity(
            id = env.id,
            projectId = env.projectId,
            name = env.name,
            device = env.device,
            androidVersion = env.androidVersion,
            appVersion = env.appVersion,
            buildNumber = env.buildNumber,
            notes = env.notes,
            createdAt = if (env.id == 0L) Instant.now() else env.createdAt
        )
        return if (env.id == 0L) {
            environmentDao.insertEnvironment(entity)
        } else {
            environmentDao.updateEnvironment(entity)
            env.id
        }
    }

    override suspend fun deleteEnvironment(env: EnvironmentProfile) {
        val entity = EnvironmentProfileEntity(
            id = env.id,
            projectId = env.projectId,
            name = env.name,
            device = env.device,
            androidVersion = env.androidVersion,
            appVersion = env.appVersion,
            buildNumber = env.buildNumber,
            notes = env.notes,
            createdAt = env.createdAt
        )
        environmentDao.deleteEnvironment(entity)
    }
}
