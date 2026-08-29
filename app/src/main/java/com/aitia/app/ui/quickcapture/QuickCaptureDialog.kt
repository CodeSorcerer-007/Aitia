package com.aitia.app.ui.quickcapture

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.ui.components.DuplicateWarningCard
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickCaptureBottomSheet(
    viewModel: QuickCaptureViewModel,
    onDismiss: () -> Unit,
    onIssueCreated: (Long) -> Unit,
    onOpenExistingIssue: (Issue) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }
    val haptic = rememberHapticFeedback()
    val extendedColors = LocalExtendedColors.current

    var projectDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Auto focus title input when modal opens
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = AitiaBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quick Capture",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary "What happened?" input
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = {
                    Text(
                        "What happened? (e.g. Camera crashes when changing photo)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AitiaBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (uiState.title.isNotBlank()) {
                            haptic.success()
                            viewModel.saveIssue { id ->
                                onIssueCreated(id)
                            }
                        }
                    }
                )
            )

            // Duplicate detection card (if matches found)
            if (uiState.duplicateMatches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                DuplicateWarningCard(
                    match = uiState.duplicateMatches.first(),
                    onOpenExisting = {
                        onDismiss()
                        onOpenExistingIssue(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Issue Type Pills
            Text(
                text = "TYPE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IssueType.entries.forEach { type ->
                    val isSelected = uiState.type == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            haptic.lightTap()
                            viewModel.onTypeSelected(type)
                        },
                        label = {
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AitiaBlue.copy(alpha = 0.2f),
                            selectedLabelColor = AitiaBlue
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = AitiaBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Priority Pills
            Text(
                text = "PRIORITY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Priority.entries.forEach { priority ->
                    val isSelected = uiState.priority == priority
                    val pColor = when (priority) {
                        Priority.LOW -> extendedColors.priorityLow
                        Priority.MEDIUM -> extendedColors.priorityMedium
                        Priority.HIGH -> extendedColors.priorityHigh
                        Priority.CRITICAL -> extendedColors.priorityCritical
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            haptic.lightTap()
                            viewModel.onPrioritySelected(priority)
                        },
                        label = {
                            Text(
                                text = priority.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = pColor.copy(alpha = 0.2f),
                            selectedLabelColor = pColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = pColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Project Selector & Screen/Area row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Project Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    val selectedProj = uiState.projects.firstOrNull { it.id == uiState.selectedProjectId }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .clickable { projectDropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedProj?.name ?: "No Project",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selectedProj != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = projectDropdownExpanded,
                        onDismissRequest = { projectDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("No Project", style = MaterialTheme.typography.bodySmall) },
                            onClick = {
                                viewModel.onProjectSelected(null)
                                projectDropdownExpanded = false
                            }
                        )
                        uiState.projects.forEach { proj ->
                            DropdownMenuItem(
                                text = { Text(proj.name, style = MaterialTheme.typography.bodySmall) },
                                onClick = {
                                    viewModel.onProjectSelected(proj.id)
                                    projectDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Screen / Feature Area
                OutlinedTextField(
                    value = uiState.screenArea,
                    onValueChange = { viewModel.onScreenAreaChange(it) },
                    placeholder = { Text("Screen / Area", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AitiaBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {
                    if (uiState.title.isNotBlank() && !uiState.isSaving) {
                        haptic.success()
                        viewModel.saveIssue { newId ->
                            onDismiss()
                            onIssueCreated(newId)
                        }
                    }
                },
                enabled = uiState.title.isNotBlank() && !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save & Continue Testing",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
