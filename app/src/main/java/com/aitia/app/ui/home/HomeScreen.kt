package com.aitia.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.domain.model.InsightType
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueType
import com.aitia.app.ui.components.AchievementBadgeDialog
import com.aitia.app.ui.components.ActiveSessionBanner
import com.aitia.app.ui.components.AitiaTopAppBar
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.components.FeatureTourDialog
import com.aitia.app.ui.components.IssueCard
import com.aitia.app.ui.components.MetricCard
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.ui.theme.StatusInvestigating
import com.aitia.app.util.rememberHapticFeedback

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToIssueDetail: (Long) -> Unit,
    onNavigateToIssues: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onQuickCaptureWithType: (IssueType) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val extendedColors = LocalExtendedColors.current
    val haptic = rememberHapticFeedback()
    var showTourDialog by remember { mutableStateOf(false) }
    var showTrophyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AitiaTopAppBar(
                title = "Aitia",
                subtitle = "Capture • Investigate • Resolve",
                activeSession = uiState.activeSession,
                onSettingsClick = onNavigateToSettings,
                actions = {
                    IconButton(onClick = {
                        haptic.lightTap()
                        showTourDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "How to Use",
                            tint = Color(0xFF00E5FF)
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Active Testing Session Banner (if running)
            if (uiState.activeSession != null) {
                item {
                    ActiveSessionBanner(
                        session = uiState.activeSession,
                        onStopSession = { viewModel.stopActiveSession(it) },
                        onClick = onNavigateToIssues
                    )
                }
            }

            // 2. Interactive Feature Tour / "How It Works" Banner
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.lightTap()
                            showTourDialog = true
                        },
                    color = Color(0xFF0D1C24)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "💡 How to Use Aitia Features",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "1-Min Guide: OCR scan, drawing, voice & sensors",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF8B949E)
                                )
                            }
                        }

                        Text(
                            text = "Explore →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }
            }

            // 3. Quick Action Shortcuts (+ Bug, + Crash, + Error, + Note)
            item {
                Column {
                    Text(
                        text = "START QUICK CAPTURE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionChip(
                            label = "+ Bug",
                            icon = Icons.Default.BugReport,
                            color = AitiaBlue,
                            onClick = { onQuickCaptureWithType(IssueType.BUG) }
                        )
                        QuickActionChip(
                            label = "+ Crash",
                            icon = Icons.Default.FlashOn,
                            color = extendedColors.priorityCritical,
                            onClick = { onQuickCaptureWithType(IssueType.CRASH) }
                        )
                        QuickActionChip(
                            label = "+ Error",
                            icon = Icons.Default.Error,
                            color = extendedColors.statusInvestigating,
                            onClick = { onQuickCaptureWithType(IssueType.ERROR) }
                        )
                        QuickActionChip(
                            label = "+ Note",
                            icon = Icons.Default.Notes,
                            color = AitiaPurple,
                            onClick = { onQuickCaptureWithType(IssueType.OTHER) }
                        )
                    }
                }
            }

            // 4. Bug Slayer Productivity & Rank Card (Love Feature!)
            val fixedCount = uiState.analyticsSummary.fixedIssues + uiState.analyticsSummary.verifiedIssues
            val slayerRank = when {
                fixedCount >= 15 -> "Αἰτία Grandmaster 👑"
                fixedCount >= 8 -> "Bug Exterminator ⚔️"
                fixedCount >= 3 -> "Bug Hunter 🏹"
                else -> "Junior Bug Scout 🐛"
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF00FF88).copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.lightTap()
                            showTrophyDialog = true
                        },
                    color = Color(0xFF0F1A15)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00FF88))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "YOUR SQUASH STATS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF88)
                                )
                            }

                            Text(
                                text = "🏆 View Trophies",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFB703),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = slayerRank,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "$fixedCount bugs squashed & resolved",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF8B949E)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF00FF88).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "🔥 Active Streak",
                                    color = Color(0xFF00FF88),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. At-a-Glance Metrics
            item {
                Column {
                    Text(
                        text = "PROJECT HEALTH",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            title = "Open",
                            value = "${uiState.analyticsSummary.openIssues}",
                            icon = Icons.Default.RadioButtonUnchecked,
                            accentColor = extendedColors.statusOpen,
                            onClick = onNavigateToIssues,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Critical",
                            value = "${uiState.analyticsSummary.criticalIssues}",
                            icon = Icons.Default.ReportProblem,
                            accentColor = extendedColors.priorityCritical,
                            onClick = onNavigateToIssues,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Crashes",
                            value = "${uiState.analyticsSummary.crashCount}",
                            icon = Icons.Default.FlashOn,
                            accentColor = extendedColors.statusInvestigating,
                            onClick = onNavigateToIssues,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Fixed",
                            value = "${uiState.analyticsSummary.fixedIssues + uiState.analyticsSummary.verifiedIssues}",
                            icon = Icons.Default.CheckCircle,
                            accentColor = extendedColors.statusFixed,
                            onClick = onNavigateToIssues,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 6. Local Diagnostic Insights Banner (Αἰτία)
            if (uiState.insights.isNotEmpty()) {
                val primaryInsight = uiState.insights.first()
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AitiaPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .then(
                                if (primaryInsight.actionableIssueId != null) {
                                    Modifier.clickable { onNavigateToIssueDetail(primaryInsight.actionableIssueId) }
                                } else Modifier
                            ),
                        color = AitiaPurple.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AitiaPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Insight",
                                    tint = AitiaPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = primaryInsight.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = primaryInsight.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 7. Pinned Issues (if any)
            if (uiState.pinnedIssues.isNotEmpty()) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = AitiaBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PINNED ISSUES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AitiaBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                items(uiState.pinnedIssues, key = { "pinned_${it.id}" }) { issue ->
                    IssueCard(
                        issue = issue,
                        onClick = { onNavigateToIssueDetail(issue.id) }
                    )
                }
            }

            // 8. Recent Activity Stream
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT TICKETS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelSmall,
                        color = AitiaBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onNavigateToIssues)
                            .padding(4.dp)
                    )
                }
            }

            if (uiState.recentIssues.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.BugReport,
                        title = "No Issues Captured Yet",
                        description = "Capture your first bug, crash, or observation in under 30 seconds using Quick Capture.",
                        actionText = "+ Capture First Issue",
                        onActionClick = { onQuickCaptureWithType(IssueType.BUG) }
                    )
                }
            } else {
                items(uiState.recentIssues, key = { "recent_${it.id}" }) { issue ->
                    IssueCard(
                        issue = issue,
                        onClick = { onNavigateToIssueDetail(issue.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showTourDialog) {
        FeatureTourDialog(onDismiss = { showTourDialog = false })
    }

    if (showTrophyDialog) {
        AchievementBadgeDialog(
            allIssues = uiState.allIssues,
            onDismiss = { showTrophyDialog = false }
        )
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val haptic = rememberHapticFeedback()
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable {
                haptic.lightTap()
                onClick()
            },
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
