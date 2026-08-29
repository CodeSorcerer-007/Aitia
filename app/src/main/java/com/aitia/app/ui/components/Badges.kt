package com.aitia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.ui.theme.LocalExtendedColors

@Composable
fun StatusBadge(status: IssueStatus, modifier: Modifier = Modifier) {
    val extendedColors = LocalExtendedColors.current
    val (color, icon) = when (status) {
        IssueStatus.OPEN -> extendedColors.statusOpen to Icons.Default.RadioButtonUnchecked
        IssueStatus.INVESTIGATING -> extendedColors.statusInvestigating to Icons.Default.Search
        IssueStatus.BLOCKED -> extendedColors.statusBlocked to Icons.Default.PauseCircle
        IssueStatus.FIXED -> extendedColors.statusFixed to Icons.Default.CheckCircle
        IssueStatus.VERIFIED -> extendedColors.statusVerified to Icons.Default.Verified
        IssueStatus.CLOSED -> extendedColors.statusClosed to Icons.Default.StopCircle
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = status.displayName,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val extendedColors = LocalExtendedColors.current
    val (color, label) = when (priority) {
        Priority.LOW -> extendedColors.priorityLow to "Low"
        Priority.MEDIUM -> extendedColors.priorityMedium to "Med"
        Priority.HIGH -> extendedColors.priorityHigh to "High"
        Priority.CRITICAL -> extendedColors.priorityCritical to "Critical"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}

@Composable
fun TypeBadge(type: IssueType, modifier: Modifier = Modifier) {
    val icon = when (type) {
        IssueType.BUG -> Icons.Default.BugReport
        IssueType.CRASH -> Icons.Default.FlashOn
        IssueType.ERROR -> Icons.Default.Error
        IssueType.UI_UX -> Icons.Default.Palette
        IssueType.PERFORMANCE -> Icons.Default.Speed
        IssueType.SECURITY -> Icons.Default.Lock
        IssueType.TEST_OBSERVATION -> Icons.Default.Visibility
        IssueType.FEATURE_IMPROVEMENT -> Icons.Default.Star
        IssueType.OTHER -> Icons.Default.Notes
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = type.displayName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}
