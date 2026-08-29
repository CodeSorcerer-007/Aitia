package com.aitia.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.util.rememberHapticFeedback

@Composable
fun StatusFlowBar(
    currentStatus: IssueStatus,
    onStatusSelected: (IssueStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()
    val scrollState = rememberScrollState()
    val statuses = IssueStatus.entries

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        statuses.forEachIndexed { index, status ->
            val isSelected = status == currentStatus
            val isPassed = status.order < currentStatus.order

            val extendedColors = LocalExtendedColors.current
            val activeColor = when (status) {
                IssueStatus.OPEN -> extendedColors.statusOpen
                IssueStatus.INVESTIGATING -> extendedColors.statusInvestigating
                IssueStatus.BLOCKED -> extendedColors.statusBlocked
                IssueStatus.FIXED -> extendedColors.statusFixed
                IssueStatus.VERIFIED -> extendedColors.statusVerified
                IssueStatus.CLOSED -> extendedColors.statusClosed
            }

            val bgColor by animateColorAsState(
                targetValue = when {
                    isSelected -> activeColor.copy(alpha = 0.20f)
                    isPassed -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.surface
                },
                label = "status_bg"
            )

            val borderColor by animateColorAsState(
                targetValue = when {
                    isSelected -> activeColor
                    isPassed -> activeColor.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                label = "status_border"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                    .clickable {
                        if (status != currentStatus) {
                            haptic.success()
                            onStatusSelected(status)
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPassed || isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isSelected) activeColor else activeColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = status.displayName,
                    color = if (isSelected) activeColor else if (isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }

            if (index < statuses.size - 1) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(12.dp)
                )
            }
        }
    }
}
