package com.aitia.app.ui.issues

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.ui.components.AitiaTopAppBar
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.components.FilterBottomSheet
import com.aitia.app.ui.components.IssueCard
import com.aitia.app.ui.components.IssueFilterState
import com.aitia.app.ui.components.SearchFilterBar
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.util.rememberHapticFeedback

@Composable
fun IssuesScreen(
    viewModel: IssuesViewModel,
    onNavigateToIssueDetail: (Long) -> Unit,
    onQuickCapture: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = rememberHapticFeedback()

    var isFilterSheetOpen by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AitiaTopAppBar(
                title = "Issues",
                subtitle = "${uiState.issues.size} ${if (uiState.issues.size == 1) "issue" else "issues"} listed"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    haptic.success()
                    onQuickCapture()
                },
                containerColor = AitiaBlue,
                contentColor = androidx.compose.ui.graphics.Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Bug",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Search and Filter Bar
            SearchFilterBar(
                searchQuery = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                filterState = uiState.filterState,
                onOpenFilterSheet = { isFilterSheetOpen = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Quick Filter Segment Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // All Active
                FilterChip(
                    selected = !uiState.filterState.hasActiveFilters && !uiState.filterState.showArchived,
                    onClick = {
                        haptic.lightTap()
                        viewModel.onFilterChange(IssueFilterState())
                    },
                    label = { Text("All Active", style = MaterialTheme.typography.labelSmall) }
                )

                // Open
                FilterChip(
                    selected = uiState.filterState.selectedStatus == IssueStatus.OPEN,
                    onClick = {
                        haptic.lightTap()
                        val newStatus = if (uiState.filterState.selectedStatus == IssueStatus.OPEN) null else IssueStatus.OPEN
                        viewModel.onFilterChange(uiState.filterState.copy(selectedStatus = newStatus))
                    },
                    label = { Text("Open", style = MaterialTheme.typography.labelSmall) }
                )

                // Investigating
                FilterChip(
                    selected = uiState.filterState.selectedStatus == IssueStatus.INVESTIGATING,
                    onClick = {
                        haptic.lightTap()
                        val newStatus = if (uiState.filterState.selectedStatus == IssueStatus.INVESTIGATING) null else IssueStatus.INVESTIGATING
                        viewModel.onFilterChange(uiState.filterState.copy(selectedStatus = newStatus))
                    },
                    label = { Text("Investigating", style = MaterialTheme.typography.labelSmall) }
                )

                // Crashes Only
                FilterChip(
                    selected = uiState.filterState.selectedType == IssueType.CRASH,
                    onClick = {
                        haptic.lightTap()
                        val newType = if (uiState.filterState.selectedType == IssueType.CRASH) null else IssueType.CRASH
                        viewModel.onFilterChange(uiState.filterState.copy(selectedType = newType))
                    },
                    label = { Text("Crashes", style = MaterialTheme.typography.labelSmall) }
                )

                // Critical Only
                FilterChip(
                    selected = uiState.filterState.selectedPriority == Priority.CRITICAL,
                    onClick = {
                        haptic.lightTap()
                        val newPriority = if (uiState.filterState.selectedPriority == Priority.CRITICAL) null else Priority.CRITICAL
                        viewModel.onFilterChange(uiState.filterState.copy(selectedPriority = newPriority))
                    },
                    label = { Text("Critical", style = MaterialTheme.typography.labelSmall) }
                )

                // Pinned Only
                FilterChip(
                    selected = uiState.filterState.onlyPinned,
                    onClick = {
                        haptic.lightTap()
                        viewModel.onFilterChange(uiState.filterState.copy(onlyPinned = !uiState.filterState.onlyPinned))
                    },
                    label = { Text("Pinned", style = MaterialTheme.typography.labelSmall) }
                )

                // Archived
                FilterChip(
                    selected = uiState.filterState.showArchived,
                    onClick = {
                        haptic.lightTap()
                        viewModel.onFilterChange(uiState.filterState.copy(showArchived = !uiState.filterState.showArchived))
                    },
                    label = { Text("Archived", style = MaterialTheme.typography.labelSmall) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sort Selector Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.issues.size} RESULTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { sortMenuExpanded = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.sortOption.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        IssueSortOption.entries.forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opt.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (opt == uiState.sortOption) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.onSortOptionSelected(opt)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Issue List
            if (uiState.issues.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.BugReport,
                    title = if (uiState.filterState.hasActiveFilters || uiState.searchQuery.isNotEmpty()) "No Matching Issues" else "No Issues Yet",
                    description = if (uiState.filterState.hasActiveFilters || uiState.searchQuery.isNotEmpty())
                        "Try adjusting your filters or search keywords."
                    else
                        "Start logging bugs and tracking investigations with Quick Capture.",
                    actionText = if (uiState.filterState.hasActiveFilters) "Reset Filters" else "+ Log New Bug",
                    onActionClick = {
                        if (uiState.filterState.hasActiveFilters) {
                            viewModel.onFilterChange(IssueFilterState())
                            viewModel.onSearchQueryChange("")
                        } else {
                            onQuickCapture()
                        }
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.issues, key = { it.id }) { issue ->
                        IssueCard(
                            issue = issue,
                            onClick = { onNavigateToIssueDetail(issue.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Filter Bottom Sheet modal
        if (isFilterSheetOpen) {
            FilterBottomSheet(
                filterState = uiState.filterState,
                projects = uiState.projects,
                onFilterChange = {
                    viewModel.onFilterChange(it)
                },
                onDismiss = { isFilterSheetOpen = false }
            )
        }
    }
}
