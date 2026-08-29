package com.aitia.app.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.ProjectVersion
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.components.IssueCard
import com.aitia.app.ui.components.MetricCard
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.ui.theme.StatusFixed
import com.aitia.app.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    viewModel: ProjectsViewModel,
    project: Project?,
    projectIssues: List<Issue>,
    onNavigateBack: () -> Unit,
    onNavigateToIssueDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val haptic = rememberHapticFeedback()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val tabs = listOf("All Issues", "Releases & Versions")

    if (project == null) {
        com.aitia.app.ui.components.AitiaLoadingScreen(
            message = "Loading project details...",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Project",
                            tint = extendedColors.priorityCritical
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Project Header Card
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = project.packageName.ifBlank { "No package declared" },
                            style = MaterialTheme.typography.labelSmall,
                            color = AitiaBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Current: v${project.currentVersion}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (project.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            title = "Open",
                            value = "${project.openIssueCount}",
                            icon = Icons.Default.RadioButtonUnchecked,
                            accentColor = extendedColors.statusOpen,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Critical",
                            value = "${project.criticalIssueCount}",
                            icon = Icons.Default.ReportProblem,
                            accentColor = extendedColors.priorityCritical,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Total",
                            value = "${project.totalIssueCount}",
                            icon = Icons.Default.Folder,
                            accentColor = AitiaBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AitiaBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            haptic.lightTap()
                            selectedTabIndex = index
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedTabIndex == 0) {
                    // All Issues Tab
                    item {
                        Text(
                            text = "PROJECT DEFECTS & ISSUES (${projectIssues.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (projectIssues.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Default.Folder,
                                title = "No Issues in ${project.name}",
                                description = "No defects logged for this project yet."
                            )
                        }
                    } else {
                        items(projectIssues, key = { it.id }) { issue ->
                            IssueCard(
                                issue = issue,
                                onClick = { onNavigateToIssueDetail(issue.id) }
                            )
                        }
                    }
                } else {
                    // Releases & Versions Grouping Tab (Section 23 & 53)
                    val groupedByVersion: Map<String, List<Issue>> = projectIssues.groupBy { issue: Issue ->
                        val verName = issue.introducedVersionName
                        if (!verName.isNullOrBlank()) verName else "v${project.currentVersion}"
                    }

                    if (groupedByVersion.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Default.Sell,
                                title = "No Version Data",
                                description = "Log app version (e.g. 1.4.2) when creating issues to group defects by release."
                            )
                        }
                    } else {
                        groupedByVersion.forEach { entry: Map.Entry<String, List<Issue>> ->
                            val versionStr = entry.key
                            val issuesInVersion = entry.value
                            item {
                                val fixedCount = issuesInVersion.count { it.status == IssueStatus.FIXED || it.status == IssueStatus.VERIFIED }
                                val openCount = issuesInVersion.count { it.status == IssueStatus.OPEN || it.status == IssueStatus.INVESTIGATING }
                                val criticalCount = issuesInVersion.count { it.priority == Priority.CRITICAL && it.status != IssueStatus.CLOSED }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 1.dp
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Release $versionStr",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${issuesInVersion.size} issues found",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AitiaBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text(
                                                text = "$fixedCount fixed",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = StatusFixed,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "$openCount open",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = extendedColors.statusOpen
                                            )
                                            if (criticalCount > 0) {
                                                Text(
                                                    text = "$criticalCount critical",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = extendedColors.priorityCritical,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            issuesInVersion.forEach { issue ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                        .clickable { onNavigateToIssueDetail(issue.id) }
                                                        .padding(8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "#${issue.id} · ${issue.title}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = issue.status.displayName,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (issue.status == IssueStatus.FIXED || issue.status == IssueStatus.VERIFIED) StatusFixed else extendedColors.statusOpen
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Project '${project.name}'?") },
                text = { Text("Are you sure you want to delete this project? Existing issues will be detached.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteProject(project)
                            showDeleteDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = extendedColors.priorityCritical)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
