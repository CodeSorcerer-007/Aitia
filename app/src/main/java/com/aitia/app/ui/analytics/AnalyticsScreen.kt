package com.aitia.app.ui.analytics

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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.InsightItem
import com.aitia.app.domain.model.InsightType
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.ui.components.AitiaTopAppBar
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.ui.theme.StatusFixed
import com.aitia.app.ui.theme.StatusInvestigating

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateToIssueDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val extendedColors = LocalExtendedColors.current
    val summary = uiState.summary

    Scaffold(
        topBar = {
            AitiaTopAppBar(
                title = "Analytics & Insights",
                subtitle = "Diagnostic intelligence and patterns"
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (uiState.totalIssues == 0) {
            EmptyStateView(
                icon = Icons.Default.Analytics,
                title = "No Defect Data Yet",
                description = "Your software debugging patterns, hotspot insights, and resolution metrics will appear here as issues are captured.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Diagnostic Insights (Αἰτία)
                if (uiState.insights.isNotEmpty()) {
                    item {
                        Text(
                            text = "DIAGNOSTIC INSIGHTS (Αἰτία)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.insights, key = { it.id }) { insight ->
                        InsightCard(insight = insight)
                    }
                }

                // 2. Defect Breakdown by Type
                item {
                    AnalyticsCard(title = "Defects by Type") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            IssueType.entries.forEach { type ->
                                val count = summary.issuesByType[type] ?: 0
                                if (count > 0) {
                                    val progress = count.toFloat() / uiState.totalIssues.toFloat()
                                    DistributionRow(
                                        label = type.displayName,
                                        count = count,
                                        progress = progress,
                                        barColor = AitiaBlue
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Breakdown by Priority
                item {
                    AnalyticsCard(title = "Severity Distribution") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Priority.entries.forEach { priority ->
                                val count = summary.issuesByPriority[priority] ?: 0
                                if (count > 0) {
                                    val progress = count.toFloat() / uiState.totalIssues.toFloat()
                                    val color = when (priority) {
                                        Priority.LOW -> extendedColors.priorityLow
                                        Priority.MEDIUM -> extendedColors.priorityMedium
                                        Priority.HIGH -> extendedColors.priorityHigh
                                        Priority.CRITICAL -> extendedColors.priorityCritical
                                    }
                                    DistributionRow(
                                        label = priority.displayName,
                                        count = count,
                                        progress = progress,
                                        barColor = color
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Resolution Velocity
                item {
                    AnalyticsCard(title = "Resolution Velocity") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Avg Resolution Time",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (summary.averageResolutionHours > 0) String.format("%.1f hours", summary.averageResolutionHours) else "N/A",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AitiaBlue
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Resolved Issues",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${summary.fixedIssues + summary.verifiedIssues} / ${uiState.totalIssues}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = extendedColors.statusFixed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun InsightCard(insight: InsightItem) {
    val extendedColors = LocalExtendedColors.current
    val (accentColor, icon) = when (insight.type) {
        InsightType.CRITICAL_ALERT -> extendedColors.priorityCritical to Icons.Default.Lightbulb
        InsightType.HOTSPOT -> StatusInvestigating to Icons.Default.Lightbulb
        InsightType.REGRESSION_RISK -> AitiaPurple to Icons.Default.Lightbulb
        InsightType.SESSION_TREND -> AitiaBlue to Icons.Default.Lightbulb
        InsightType.POSITIVE_PROGRESS -> StatusFixed to Icons.Default.Lightbulb
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
        color = accentColor.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    content: @Composable () -> Unit
) {
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
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun DistributionRow(
    label: String,
    count: Int,
    progress: Float,
    barColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
