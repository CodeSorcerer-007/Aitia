package com.aitia.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.util.rememberHapticFeedback

enum class BottomNavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    ISSUES("issues", "Issues", Icons.Filled.BugReport, Icons.Outlined.BugReport),
    PROJECTS("projects", "Projects", Icons.Filled.Folder, Icons.Outlined.Folder),
    ANALYTICS("analytics", "Insights", Icons.Filled.Analytics, Icons.Outlined.Analytics)
}

@Composable
fun AitiaBottomNavigation(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit,
    onQuickCaptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home Tab
            BottomNavItem(
                destination = BottomNavDestination.HOME,
                isSelected = currentRoute == BottomNavDestination.HOME.route,
                onClick = {
                    haptic.lightTap()
                    onNavigateToRoute(BottomNavDestination.HOME.route)
                }
            )

            // 2. Issues Tab
            BottomNavItem(
                destination = BottomNavDestination.ISSUES,
                isSelected = currentRoute == BottomNavDestination.ISSUES.route,
                onClick = {
                    haptic.lightTap()
                    onNavigateToRoute(BottomNavDestination.ISSUES.route)
                }
            )

            // Center Elevated Quick Capture FAB
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(AitiaBlue, AitiaPurple)
                        )
                    )
                    .clickable {
                        haptic.success()
                        onQuickCaptureClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Capture",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 3. Projects Tab
            BottomNavItem(
                destination = BottomNavDestination.PROJECTS,
                isSelected = currentRoute == BottomNavDestination.PROJECTS.route,
                onClick = {
                    haptic.lightTap()
                    onNavigateToRoute(BottomNavDestination.PROJECTS.route)
                }
            )

            // 4. Analytics / Insights Tab
            BottomNavItem(
                destination = BottomNavDestination.ANALYTICS,
                isSelected = currentRoute == BottomNavDestination.ANALYTICS.route,
                onClick = {
                    haptic.lightTap()
                    onNavigateToRoute(BottomNavDestination.ANALYTICS.route)
                }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
            contentDescription = destination.title,
            tint = if (isSelected) AitiaBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = destination.title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AitiaBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
