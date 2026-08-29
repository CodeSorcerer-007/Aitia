package com.aitia.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.Project
import com.aitia.app.ui.theme.AitiaBlue

data class IssueFilterState(
    val selectedType: IssueType? = null,
    val selectedStatus: IssueStatus? = null,
    val selectedPriority: Priority? = null,
    val selectedProjectId: Long? = null,
    val onlyPinned: Boolean = false,
    val showArchived: Boolean = false
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedType != null) count++
            if (selectedStatus != null) count++
            if (selectedPriority != null) count++
            if (selectedProjectId != null) count++
            if (onlyPinned) count++
            if (showArchived) count++
            return count
        }

    val hasActiveFilters: Boolean
        get() = activeFilterCount > 0
}

@Composable
fun SearchFilterBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    filterState: IssueFilterState,
    onOpenFilterSheet: () -> Unit,
    placeholder: String = "Search bugs, logs, exceptions...",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AitiaBlue,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Filter Button with Badge counter
        BadgedBox(
            badge = {
                if (filterState.activeFilterCount > 0) {
                    Badge(
                        containerColor = AitiaBlue,
                        contentColor = Color.White
                    ) {
                        Text("${filterState.activeFilterCount}")
                    }
                }
            }
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (filterState.hasActiveFilters) AitiaBlue else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(onClick = onOpenFilterSheet),
                color = if (filterState.hasActiveFilters) AitiaBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters",
                        tint = if (filterState.hasActiveFilters) AitiaBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterState: IssueFilterState,
    projects: List<Project>,
    onFilterChange: (IssueFilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Issues",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (filterState.hasActiveFilters) {
                    Text(
                        text = "Reset All",
                        style = MaterialTheme.typography.labelMedium,
                        color = AitiaBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onFilterChange(IssueFilterState()) }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Status Filter
            Text("STATUS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IssueStatus.entries.forEach { status ->
                    FilterChip(
                        selected = filterState.selectedStatus == status,
                        onClick = {
                            val newStatus = if (filterState.selectedStatus == status) null else status
                            onFilterChange(filterState.copy(selectedStatus = newStatus))
                        },
                        label = { Text(status.displayName, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Priority Filter
            Text("PRIORITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = filterState.selectedPriority == priority,
                        onClick = {
                            val newPriority = if (filterState.selectedPriority == priority) null else priority
                            onFilterChange(filterState.copy(selectedPriority = newPriority))
                        },
                        label = { Text(priority.displayName, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Issue Type Filter
            Text("ISSUE TYPE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IssueType.entries.forEach { type ->
                    FilterChip(
                        selected = filterState.selectedType == type,
                        onClick = {
                            val newType = if (filterState.selectedType == type) null else type
                            onFilterChange(filterState.copy(selectedType = newType))
                        },
                        label = { Text(type.displayName, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            if (projects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                // 4. Project Filter
                Text("PROJECT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    projects.forEach { project ->
                        FilterChip(
                            selected = filterState.selectedProjectId == project.id,
                            onClick = {
                                val newProj = if (filterState.selectedProjectId == project.id) null else project.id
                                onFilterChange(filterState.copy(selectedProjectId = newProj))
                            },
                            label = { Text(project.name, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
