package com.aitia.app.ui.projects

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.EnvironmentProfile
import com.aitia.app.domain.model.Project
import com.aitia.app.domain.model.TestingSession
import com.aitia.app.ui.components.AitiaTopAppBar
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.ui.theme.StatusFixed
import com.aitia.app.util.DateFormatter
import com.aitia.app.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: ProjectsViewModel,
    onNavigateToProjectDetail: (Long) -> Unit,
    onNavigateToSessionSummary: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = rememberHapticFeedback()
    val extendedColors = LocalExtendedColors.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var showCreateEnvDialog by remember { mutableStateOf(false) }
    var showStartSessionDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Projects", "Environments", "Testing Sessions")

    Scaffold(
        topBar = {
            AitiaTopAppBar(
                title = "Projects & Tools",
                subtitle = "Apps, test devices, and testing sessions"
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Projects Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ACTIVE PROJECTS (${uiState.projects.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { showCreateProjectDialog = true }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ New Project", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        if (uiState.projects.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Default.Folder,
                                    title = "No Projects Yet",
                                    description = "Create a project for each application you are developing or testing.",
                                    actionText = "+ Add First Project",
                                    onActionClick = { showCreateProjectDialog = true }
                                )
                            }
                        } else {
                            items(uiState.projects, key = { it.id }) { project ->
                                ProjectItemCard(
                                    project = project,
                                    onClick = { onNavigateToProjectDetail(project.id) }
                                )
                            }
                        }
                    }

                    1 -> {
                        // Environments Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TEST DEVICE PROFILES (${uiState.environments.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { showCreateEnvDialog = true }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ New Profile", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        if (uiState.environments.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Default.Smartphone,
                                    title = "No Environment Profiles",
                                    description = "Save your test devices (e.g., Pixel 8 / Android 15) to auto-fill bug metadata.",
                                    actionText = "+ Add Test Environment",
                                    onActionClick = { showCreateEnvDialog = true }
                                )
                            }
                        } else {
                            items(uiState.environments, key = { it.id }) { env ->
                                EnvironmentItemCard(
                                    env = env,
                                    onDelete = { viewModel.deleteEnvironment(env) }
                                )
                            }
                        }
                    }

                    2 -> {
                        // Testing Sessions Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TESTING SESSIONS (${uiState.sessions.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { showStartSessionDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Start Session", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        if (uiState.sessions.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Default.Schedule,
                                    title = "No Testing Sessions",
                                    description = "Start a testing session when doing QA. All bugs created during the session are linked automatically.",
                                    actionText = "Start First Session",
                                    onActionClick = { showStartSessionDialog = true }
                                )
                            }
                        } else {
                            items(uiState.sessions, key = { it.id }) { session ->
                                TestingSessionItemCard(
                                    session = session,
                                    onClick = {
                                        if (session.endedAt != null) {
                                            onNavigateToSessionSummary(session.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Create Project Dialog
        if (showCreateProjectDialog) {
            var name by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }
            var pkg by remember { mutableStateOf("") }
            var version by remember { mutableStateOf("1.0.0") }

            AlertDialog(
                onDismissRequest = { showCreateProjectDialog = false },
                title = { Text("Create New Project") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Project Name") },
                            placeholder = { Text("e.g. WeatherApp") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = pkg,
                            onValueChange = { pkg = it },
                            label = { Text("Package / App Name") },
                            placeholder = { Text("com.example.weather") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = version,
                            onValueChange = { version = it },
                            label = { Text("Initial Version") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.createProject(name, desc, pkg, version, "#58A6FF")
                                showCreateProjectDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateProjectDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Create Environment Dialog
        if (showCreateEnvDialog) {
            var name by remember { mutableStateOf("") }
            var device by remember { mutableStateOf("Pixel 8") }
            var androidVersion by remember { mutableStateOf("Android 15") }
            var appVersion by remember { mutableStateOf("1.0.0") }

            AlertDialog(
                onDismissRequest = { showCreateEnvDialog = false },
                title = { Text("Add Test Environment") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Profile Name") },
                            placeholder = { Text("Pixel 8 — Main QA") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = device,
                            onValueChange = { device = it },
                            label = { Text("Device Model") },
                            placeholder = { Text("Pixel 8 Pro") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = androidVersion,
                            onValueChange = { androidVersion = it },
                            label = { Text("Android OS Version") },
                            placeholder = { Text("Android 15 (API 35)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = appVersion,
                            onValueChange = { appVersion = it },
                            label = { Text("App Version") },
                            placeholder = { Text("1.4.2") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.createEnvironment(name, device, androidVersion, appVersion, null)
                                showCreateEnvDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Add Profile")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateEnvDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Start Testing Session Dialog
        if (showStartSessionDialog) {
            var sessionName by remember { mutableStateOf("") }
            var selectedProjId by remember { mutableStateOf<Long?>(null) }
            var selectedEnvId by remember { mutableStateOf<Long?>(null) }

            AlertDialog(
                onDismissRequest = { showStartSessionDialog = false },
                title = { Text("Start Testing Session") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = sessionName,
                            onValueChange = { sessionName = it },
                            label = { Text("Session Name") },
                            placeholder = { Text("e.g. Login Flow QA") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Link Project (Optional)", style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            uiState.projects.take(3).forEach { proj ->
                                FilterChip(
                                    selected = selectedProjId == proj.id,
                                    onClick = { selectedProjId = if (selectedProjId == proj.id) null else proj.id },
                                    label = { Text(proj.name, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Text("Select Environment (Optional)", style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            uiState.environments.take(2).forEach { env ->
                                FilterChip(
                                    selected = selectedEnvId == env.id,
                                    onClick = { selectedEnvId = if (selectedEnvId == env.id) null else env.id },
                                    label = { Text(env.device, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (sessionName.isNotBlank()) {
                                viewModel.startTestingSession(sessionName, selectedProjId, selectedEnvId)
                                showStartSessionDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Start Now")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartSessionDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProjectItemCard(
    project: Project,
    onClick: () -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "v${project.currentVersion}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AitiaBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (project.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "${project.openIssueCount} Open",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (project.openIssueCount > 0) extendedColors.statusOpen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (project.criticalIssueCount > 0) {
                        Text(
                            text = "${project.criticalIssueCount} Critical",
                            style = MaterialTheme.typography.labelSmall,
                            color = extendedColors.priorityCritical,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${project.totalIssueCount} Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EnvironmentItemCard(
    env: EnvironmentProfile,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = env.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${env.device} · ${env.androidVersion} · App v${env.appVersion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TestingSessionItemCard(
    session: TestingSession,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (session.isActive) AitiaBlue else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        color = if (session.isActive) AitiaBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (session.isActive) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AitiaBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = session.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${DateFormatter.formatAbsolute(session.startedAt)} · Duration: ${session.formattedDuration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${session.totalIssuesFound} issues recorded (${session.crashesFound} crashes, ${session.bugsFound} bugs)",
                    style = MaterialTheme.typography.labelSmall,
                    color = AitiaBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
