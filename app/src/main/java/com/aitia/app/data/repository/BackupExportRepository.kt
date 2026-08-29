package com.aitia.app.data.repository

import android.content.Context
import com.aitia.app.data.local.AitiaDatabase
import com.aitia.app.data.local.entity.ChecklistItemEntity
import com.aitia.app.data.local.entity.EnvironmentProfileEntity
import com.aitia.app.data.local.entity.IssueEntity
import com.aitia.app.data.local.entity.IssueNoteEntity
import com.aitia.app.data.local.entity.ProjectEntity
import com.aitia.app.data.local.entity.ProjectVersionEntity
import com.aitia.app.data.local.entity.TagEntity
import com.aitia.app.data.local.entity.TestingSessionEntity
import com.aitia.app.domain.model.Issue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant

@Serializable
data class AitiaBackupDto(
    val version: Int = 1,
    val exportedAt: String,
    val projects: List<ProjectBackupDto> = emptyList(),
    val issues: List<IssueBackupDto> = emptyList(),
    val environments: List<EnvironmentBackupDto> = emptyList(),
    val sessions: List<SessionBackupDto> = emptyList(),
    val tags: List<String> = emptyList()
)

@Serializable
data class ProjectBackupDto(
    val id: Long,
    val name: String,
    val description: String = "",
    val packageName: String = "",
    val platform: String = "Android",
    val currentVersion: String = "1.0.0",
    val colorHex: String = "#58A6FF"
)

@Serializable
data class IssueBackupDto(
    val id: Long,
    val projectId: Long? = null,
    val title: String,
    val description: String = "",
    val type: String,
    val status: String,
    val priority: String,
    val screen: String = "",
    val stepsToReproduce: String = "",
    val expectedBehavior: String = "",
    val actualBehavior: String = "",
    val technicalDetails: String = "",
    val exceptionType: String = "",
    val errorMessage: String = "",
    val sourceFile: String = "",
    val sourceLine: String = "",
    val suspectedCause: String = "",
    val solution: String = "",
    val verification: String = "",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val notes: List<String> = emptyList(),
    val checklist: List<ChecklistBackupDto> = emptyList()
)

@Serializable
data class ChecklistBackupDto(
    val text: String,
    val isCompleted: Boolean = false
)

@Serializable
data class EnvironmentBackupDto(
    val id: Long,
    val name: String,
    val device: String,
    val androidVersion: String,
    val appVersion: String
)

@Serializable
data class SessionBackupDto(
    val id: Long,
    val name: String,
    val startedAt: String,
    val endedAt: String? = null,
    val notes: String = ""
)

class BackupExportRepository(
    private val context: Context,
    private val database: AitiaDatabase,
    private val issueRepository: IssueRepository,
    private val projectRepository: ProjectRepository
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val projects = projectRepository.getAllProjects().first()
        val issues = issueRepository.getActiveIssues().first() + issueRepository.getArchivedIssues().first()
        val envs = database.environmentDao().getAllEnvironments().first()
        val sessions = database.testingSessionDao().getAllSessions().first()
        val tags = database.tagDao().getAllTags().first().map { it.name }

        val issueDtos = issues.map { issue ->
            val notes = database.noteDao().getNotesForIssue(issue.id).first().map { it.text }
            val checklist = database.checklistDao().getChecklistForIssue(issue.id).first().map {
                ChecklistBackupDto(it.text, it.isCompleted)
            }
            IssueBackupDto(
                id = issue.id,
                projectId = issue.projectId,
                title = issue.title,
                description = issue.description,
                type = issue.type.name,
                status = issue.status.name,
                priority = issue.priority.name,
                screen = issue.screen,
                stepsToReproduce = issue.stepsToReproduce,
                expectedBehavior = issue.expectedBehavior,
                actualBehavior = issue.actualBehavior,
                technicalDetails = issue.technicalDetails,
                exceptionType = issue.exceptionType,
                errorMessage = issue.errorMessage,
                sourceFile = issue.sourceFile,
                sourceLine = issue.sourceLine,
                suspectedCause = issue.suspectedCause,
                solution = issue.solution,
                verification = issue.verification,
                isPinned = issue.isPinned,
                isArchived = issue.isArchived,
                notes = notes,
                checklist = checklist
            )
        }

        val backup = AitiaBackupDto(
            version = 1,
            exportedAt = Instant.now().toString(),
            projects = projects.map {
                ProjectBackupDto(it.id, it.name, it.description, it.packageName, it.platform, it.currentVersion, it.colorHex)
            },
            issues = issueDtos,
            environments = envs.map {
                EnvironmentBackupDto(it.id, it.name, it.device, it.androidVersion, it.appVersion)
            },
            sessions = sessions.map {
                SessionBackupDto(it.id, it.name, it.startedAt.toString(), it.endedAt?.toString(), it.notes)
            },
            tags = tags
        )

        json.encodeToString(backup)
    }

    suspend fun importFromJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val backup = json.decodeFromString<AitiaBackupDto>(jsonString)
            var importedCount = 0

            // Insert Projects
            for (p in backup.projects) {
                database.projectDao().insertProject(
                    ProjectEntity(
                        id = p.id,
                        name = p.name,
                        description = p.description,
                        packageName = p.packageName,
                        platform = p.platform,
                        currentVersion = p.currentVersion,
                        colorHex = p.colorHex
                    )
                )
            }

            // Insert Environments
            for (e in backup.environments) {
                database.environmentDao().insertEnvironment(
                    EnvironmentProfileEntity(
                        id = e.id,
                        name = e.name,
                        device = e.device,
                        androidVersion = e.androidVersion,
                        appVersion = e.appVersion
                    )
                )
            }

            // Insert Issues
            for (i in backup.issues) {
                val newIssueId = database.issueDao().insertIssue(
                    IssueEntity(
                        id = i.id,
                        projectId = i.projectId,
                        title = i.title,
                        description = i.description,
                        type = com.aitia.app.domain.model.IssueType.fromString(i.type),
                        status = com.aitia.app.domain.model.IssueStatus.fromString(i.status),
                        priority = com.aitia.app.domain.model.Priority.fromString(i.priority),
                        screen = i.screen,
                        stepsToReproduce = i.stepsToReproduce,
                        expectedBehavior = i.expectedBehavior,
                        actualBehavior = i.actualBehavior,
                        technicalDetails = i.technicalDetails,
                        exceptionType = i.exceptionType,
                        errorMessage = i.errorMessage,
                        sourceFile = i.sourceFile,
                        sourceLine = i.sourceLine,
                        suspectedCause = i.suspectedCause,
                        solution = i.solution,
                        verification = i.verification,
                        isPinned = i.isPinned,
                        isArchived = i.isArchived
                    )
                )

                for (note in i.notes) {
                    database.noteDao().insertNote(
                        IssueNoteEntity(issueId = newIssueId, text = note)
                    )
                }

                for ((idx, chk) in i.checklist.withIndex()) {
                    database.checklistDao().insertChecklistItem(
                        ChecklistItemEntity(
                            issueId = newIssueId,
                            text = chk.text,
                            isCompleted = chk.isCompleted,
                            position = idx
                        )
                    )
                }
                importedCount++
            }

            importedCount
        }
    }

    fun generateMarkdownReport(issues: List<Issue>, projectName: String = "Aitia Report"): String {
        val sb = StringBuilder()
        sb.appendLine("# $projectName — Developer Defect & Debugging Report")
        sb.appendLine("> Generated by Aitia (Αἰτία) on ${Instant.now()}")
        sb.appendLine()
        sb.appendLine("## Summary")
        sb.appendLine("- **Total Issues:** ${issues.size}")
        sb.appendLine("- **Open / In Progress:** ${issues.count { !it.isResolved }}")
        sb.appendLine("- **Fixed / Verified:** ${issues.count { it.isResolved }}")
        sb.appendLine("- **Crashes:** ${issues.count { it.type.name == "CRASH" }}")
        sb.appendLine("- **Critical Severity:** ${issues.count { it.priority.name == "CRITICAL" }}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()

        for ((idx, issue) in issues.withIndex()) {
            sb.appendLine("### ${idx + 1}. [${issue.type.displayName}] ${issue.title}")
            sb.appendLine("- **Status:** `${issue.status.displayName}` | **Priority:** `${issue.priority.displayName}` | **Project:** ${issue.projectName ?: "None"}")
            if (issue.screen.isNotBlank()) {
                sb.appendLine("- **Screen / Area:** ${issue.screen}")
            }
            if (issue.description.isNotBlank()) {
                sb.appendLine()
                sb.appendLine("**Description:**")
                sb.appendLine(issue.description)
            }
            if (issue.stepsToReproduce.isNotBlank()) {
                sb.appendLine()
                sb.appendLine("**Steps to Reproduce:**")
                sb.appendLine(issue.stepsToReproduce)
            }
            if (issue.expectedBehavior.isNotBlank() || issue.actualBehavior.isNotBlank()) {
                sb.appendLine()
                sb.appendLine("**Expected:** ${issue.expectedBehavior}")
                sb.appendLine("**Actual:** ${issue.actualBehavior}")
            }
            if (issue.technicalDetails.isNotBlank()) {
                sb.appendLine()
                sb.appendLine("**Logs / Stack Trace:**")
                sb.appendLine("```text")
                sb.appendLine(issue.technicalDetails)
                sb.appendLine("```")
            }
            if (issue.suspectedCause.isNotBlank()) {
                sb.appendLine()
                sb.appendLine("**Suspected Cause (Αἰτία):**")
                sb.appendLine(issue.suspectedCause)
            }
            if (issue.solution.isNotBlank()) {
                sb.appendLine()
                sb.appendLine("**Fix / Solution:**")
                sb.appendLine(issue.solution)
            }
            if (issue.verification.isNotBlank()) {
                sb.appendLine()
                sb.appendLine("**Verification:**")
                sb.appendLine(issue.verification)
            }
            sb.appendLine()
            sb.appendLine("---")
            sb.appendLine()
        }

        return sb.toString()
    }

    fun generateCsvExport(issues: List<Issue>): String {
        val sb = StringBuilder()
        sb.appendLine("ID,Title,Type,Status,Priority,Project,Screen,SuspectedCause,Solution,CreatedAt,ResolvedAt")
        for (i in issues) {
            val title = "\"${i.title.replace("\"", "\"\"")}\""
            val project = "\"${(i.projectName ?: "").replace("\"", "\"\"")}\""
            val screen = "\"${i.screen.replace("\"", "\"\"")}\""
            val cause = "\"${i.suspectedCause.replace("\"", "\"\"")}\""
            val fix = "\"${i.solution.replace("\"", "\"\"")}\""
            sb.appendLine("${i.id},$title,${i.type.name},${i.status.name},${i.priority.name},$project,$screen,$cause,$fix,${i.createdAt},${i.resolvedAt ?: ""}")
        }
        return sb.toString()
    }

    suspend fun writeExportFile(content: String, filename: String): File = withContext(Dispatchers.IO) {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, filename)
        file.writeText(content)
        file
    }
}
