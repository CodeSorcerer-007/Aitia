package com.aitia.app.ui.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.TestingSession
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.components.IssueCard
import com.aitia.app.ui.components.MetricCard
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.util.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestingSessionSummaryScreen(
    session: TestingSession?,
    sessionIssues: List<Issue>,
    onNavigateBack: () -> Unit,
    onNavigateToIssueDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current

    if (session == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AitiaBlue)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Session Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Session Overview Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = session.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Project: ${session.projectName ?: "Unassigned"} · Env: ${session.environmentName ?: "Default"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Duration: ${session.formattedDuration} · Started: ${DateFormatter.formatAbsolute(session.startedAt)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AitiaBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (session.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = session.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 2. Metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Issues Found",
                        value = "${sessionIssues.size}",
                        icon = Icons.Default.BugReport,
                        accentColor = AitiaBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Crashes",
                        value = "${sessionIssues.count { it.type.name == "CRASH" }}",
                        icon = Icons.Default.FlashOn,
                        accentColor = extendedColors.priorityCritical,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Duration",
                        value = session.formattedDuration,
                        icon = Icons.Default.Schedule,
                        accentColor = extendedColors.statusFixed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Issues Found List
            item {
                Text(
                    text = "RECORDED DEFECTS (${sessionIssues.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            if (sessionIssues.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.CheckCircle,
                        title = "Zero Defects Logged",
                        description = "No bugs or crashes recorded during this session."
                    )
                }
            } else {
                items(sessionIssues, key = { it.id }) { issue ->
                    IssueCard(
                        issue = issue,
                        onClick = { onNavigateToIssueDetail(issue.id) }
                    )
                }
            }
        }
    }
}
