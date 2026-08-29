package com.aitia.app.ui.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aitia.app.domain.model.Attachment
import com.aitia.app.domain.model.ChecklistItem
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueNote
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.RelationshipType
import com.aitia.app.domain.model.Tag
import com.aitia.app.domain.model.TimelineEvent
import com.aitia.app.domain.similarity.PreviousFixMatcher
import com.aitia.app.ui.components.AitiaCameraCaptureDialog
import com.aitia.app.ui.components.AudioGlitchRecorderDialog
import com.aitia.app.ui.components.BarcodeScannerDialog
import com.aitia.app.ui.components.BugCardShareDialog
import com.aitia.app.ui.components.ChecklistComponent
import com.aitia.app.ui.components.CodeViewer
import com.aitia.app.ui.components.CrossDeviceVerificationMatrix
import com.aitia.app.ui.components.DeviceVitalsCard
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.components.GitCommitDialog
import com.aitia.app.ui.components.NetworkCurlInspectorDialog
import com.aitia.app.ui.components.PreviousFixBanner
import com.aitia.app.ui.components.PriorityBadge
import com.aitia.app.ui.components.RootCauseDiagnosisCard
import com.aitia.app.ui.components.ScanStackTraceDialog
import com.aitia.app.ui.components.StatusBadge
import com.aitia.app.ui.components.StatusFlowBar
import com.aitia.app.ui.components.TypeBadge
import com.aitia.app.ui.components.VisualRegressionCompareDialog
import com.aitia.app.ui.components.VoiceReproStepsDialog
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.ui.theme.MonospaceCode
import com.aitia.app.ui.theme.StatusFixed
import com.aitia.app.util.DateFormatter
import com.aitia.app.util.ShareHelper
import com.aitia.app.util.rememberHapticFeedback
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IssueDetailScreen(
    issueId: Long,
    viewModel: IssueDetailViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()
    val extendedColors = LocalExtendedColors.current

    LaunchedEffect(issueId) {
        viewModel.setIssueId(issueId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val issue = uiState.issue

    val notes by viewModel.getNotesFlow(issueId).collectAsState(initial = emptyList())
    val attachments by viewModel.getAttachmentsFlow(issueId).collectAsState(initial = emptyList())
    val checklist by viewModel.getChecklistFlow(issueId).collectAsState(initial = emptyList())
    val relatedIssues by viewModel.getRelatedFlow(issueId).collectAsState(initial = emptyList())
    val timeline by viewModel.getTimelineFlow(issueId).collectAsState(initial = emptyList())
    val tags by viewModel.getTagsFlow(issueId).collectAsState(initial = emptyList())
    val allTags by viewModel.getAllTagsFlow().collectAsState(initial = emptyList())

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddRelationDialog by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var viewingAttachment by remember { mutableStateOf<Attachment?>(null) }
    var showResolutionPrompt by remember { mutableStateOf(false) }

    // Advanced Developer & QA Real-Life Features Dialog States
    var showCameraCaptureDialog by remember { mutableStateOf(false) }
    var showScanOcrDialog by remember { mutableStateOf(false) }
    var showVisualRegressionDialog by remember { mutableStateOf(false) }
    var regressionActualImageFile by remember { mutableStateOf<File?>(null) }
    var showVoiceStepsDialog by remember { mutableStateOf(false) }
    var showAudioRecorderDialog by remember { mutableStateOf(false) }
    var showCurlInspectorDialog by remember { mutableStateOf(false) }
    var showGitCommitDialog by remember { mutableStateOf(false) }
    var showBarcodeScannerDialog by remember { mutableStateOf(false) }
    var showBugCardShareDialog by remember { mutableStateOf(false) }

    // Native file / media pickers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            haptic.success()
            val filename = uri.lastPathSegment?.substringAfterLast('/') ?: "screenshot_${System.currentTimeMillis()}.png"
            viewModel.addAttachment(
                filename = filename,
                uriPath = uri.toString(),
                mimeType = "image/png",
                sizeBytes = 102400
            )
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            haptic.success()
            val filename = uri.lastPathSegment?.substringAfterLast('/') ?: "logfile_${System.currentTimeMillis()}.txt"
            viewModel.addAttachment(
                filename = filename,
                uriPath = uri.toString(),
                mimeType = "text/plain",
                sizeBytes = 20480
            )
        }
    }

    val tabs = listOf("Overview", "Logs & Diagnostics", "Investigation & Tasks", "Root Cause & Fix", "History & Relations")

    if (issue == null) {
        com.aitia.app.ui.components.AitiaLoadingScreen(
            message = "Loading defect workbench...",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val matchedFixes = remember(issue, uiState.allIssues) {
        PreviousFixMatcher.findSimilarResolvedFixes(issue, uiState.allIssues)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "#${issue.id} · ${issue.type.displayName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Export Visual Bug Card (PNG)
                    IconButton(onClick = {
                        haptic.lightTap()
                        showBugCardShareDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export Bug Card",
                            tint = Color(0xFF00FF88)
                        )
                    }

                    // Pin toggle
                    IconButton(onClick = {
                        haptic.lightTap()
                        viewModel.togglePinned()
                    }) {
                        Icon(
                            imageVector = if (issue.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (issue.isPinned) AitiaBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Archive toggle
                    IconButton(onClick = {
                        haptic.lightTap()
                        viewModel.toggleArchived()
                    }) {
                        Icon(
                            imageVector = if (issue.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                            contentDescription = "Archive",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Delete
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = extendedColors.priorityCritical
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // 1. Status Flow Pipeline Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "LIFECYCLE STATUS PIPELINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusFlowBar(
                        currentStatus = issue.status,
                        onStatusSelected = { newStatus ->
                            viewModel.updateStatus(newStatus)
                            if (newStatus == IssueStatus.FIXED || newStatus == IssueStatus.VERIFIED) {
                                showResolutionPrompt = true
                            }
                        }
                    )
                }
            }

            // 2. Title & Tags Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TypeBadge(type = issue.type)
                        PriorityBadge(priority = issue.priority)
                        StatusBadge(status = issue.status)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = issue.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Tags row
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tags.forEach { tag ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { viewModel.removeTag(tag.id) },
                                color = AitiaBlue.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${tag.name}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AitiaBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Tag",
                                        tint = AitiaBlue,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "+ Tag",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { showAddTagDialog = true }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (issue.projectName != null) "Project: ${issue.projectName}" else "No Project",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Created: ${DateFormatter.formatAbsolute(issue.createdAt)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AitiaBlue,
                edgePadding = 16.dp
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

            // 4. Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Overview Tab
                        // 1. Previous Fix Memory Matcher Banner
                        if (matchedFixes.isNotEmpty()) {
                            item {
                                PreviousFixBanner(
                                    matchedFix = matchedFixes.first(),
                                    onApplySolution = { sol ->
                                        viewModel.updateIssueField { it.copy(solution = sol) }
                                    },
                                    onNavigateToIssue = { targetId ->
                                        viewModel.setIssueId(targetId)
                                    }
                                )
                            }
                        }

                        // 2. Root Cause Diagnostician Card
                        item {
                            RootCauseDiagnosisCard(
                                exceptionType = issue.exceptionType,
                                errorMessage = issue.errorMessage.ifBlank { issue.technicalDetails },
                                onApplySuggestedFix = { codeSnippet ->
                                    viewModel.updateIssueField { it.copy(solution = codeSnippet) }
                                }
                            )
                        }

                        // 3. Hardware & System Vitals Card
                        item {
                            DeviceVitalsCard(
                                onSnapshotCaptured = { snapshot ->
                                    viewModel.appendDeviceVitals(context)
                                }
                            )
                        }

                        // 4. Description
                        item {
                            DetailSectionCard(title = "Description") {
                                OutlinedTextField(
                                    value = issue.description,
                                    onValueChange = { newDesc ->
                                        viewModel.updateIssueField { it.copy(description = newDesc) }
                                    },
                                    placeholder = { Text("Add detailed bug description...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AitiaBlue,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        // 5. Screen / Feature Area
                        item {
                            DetailSectionCard(title = "Screen / Feature Area") {
                                OutlinedTextField(
                                    value = issue.screen,
                                    onValueChange = { newScreen ->
                                        viewModel.updateIssueField { it.copy(screen = newScreen) }
                                    },
                                    placeholder = { Text("e.g. Profile -> Edit Photo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AitiaBlue,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        // 6. Steps to Reproduce (With Voice Dictation trigger)
                        item {
                            DetailSectionCard(
                                title = "Steps to Reproduce",
                                headerAction = {
                                    TextButton(onClick = { showVoiceStepsDialog = true }) {
                                        Text("🎙️ Dictate Steps", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00F0FF))
                                    }
                                }
                            ) {
                                OutlinedTextField(
                                    value = issue.stepsToReproduce,
                                    onValueChange = { newSteps ->
                                        viewModel.updateIssueField { it.copy(stepsToReproduce = newSteps) }
                                    },
                                    placeholder = { Text("1. Open screen\n2. Tap action\n3. Observe problem") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AitiaBlue,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        // 7. Expected vs Actual Behavior
                        item {
                            DetailSectionCard(title = "Expected vs Actual Behavior") {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = issue.expectedBehavior,
                                        onValueChange = { newExp ->
                                            viewModel.updateIssueField { it.copy(expectedBehavior = newExp) }
                                        },
                                        label = { Text("Expected Behavior") },
                                        placeholder = { Text("What should have happened?") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AitiaBlue,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    OutlinedTextField(
                                        value = issue.actualBehavior,
                                        onValueChange = { newAct ->
                                            viewModel.updateIssueField { it.copy(actualBehavior = newAct) }
                                        },
                                        label = { Text("Actual Behavior") },
                                        placeholder = { Text("What actually happened?") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = extendedColors.priorityCritical,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Logs & Diagnostics Tab
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // Diagnostic Action Chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showScanOcrDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("📸 Scan Terminal OCR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            haptic.success()
                                            viewModel.harvestLogcatToTechnicalDetails()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("📋 Dump Recent Logcat", color = Color.White, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            haptic.success()
                                            viewModel.appendDeviceVitals(context)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("⚡ Snapshot Vitals", color = Color.White, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = { showCurlInspectorDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("🌐 cURL / HTTP", color = Color(0xFF00FF88), fontSize = 12.sp)
                                    }
                                }

                                CodeViewer(
                                    codeText = issue.technicalDetails,
                                    onCodeChange = { newLogs ->
                                        viewModel.updateIssueField { it.copy(technicalDetails = newLogs) }
                                    },
                                    onParseStackTrace = {
                                        haptic.success()
                                        viewModel.parseLogsAndAutoPopulate()
                                    }
                                )

                                // Parsed diagnostics breakdown
                                DetailSectionCard(title = "Extracted Diagnostics") {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Exception: ",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = issue.exceptionType.ifBlank { "Not detected" },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (issue.exceptionType.isNotBlank()) extendedColors.priorityCritical else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Source File: ",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = if (issue.sourceFile.isNotBlank()) "${issue.sourceFile}:${issue.sourceLine}" else "Not detected",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (issue.sourceFile.isNotBlank()) AitiaBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Error Message: ",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = issue.errorMessage.ifBlank { "None" },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Investigation & Tasks Tab
                        item {
                            DetailSectionCard(
                                title = "Investigation Journal",
                                headerAction = {
                                    Row {
                                        TextButton(onClick = { showVoiceStepsDialog = true }) {
                                            Text("🎙️ Dictate", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00F0FF))
                                        }
                                        TextButton(onClick = { showAddNoteDialog = true }) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("+ Note", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            ) {
                                if (notes.isEmpty()) {
                                    Text(
                                        text = "No debugging notes added yet. Record observations as you investigate.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        notes.forEach { note ->
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = DateFormatter.formatAbsolute(note.createdAt),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = AitiaBlue,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = note.text,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            haptic.lightTap()
                                                            viewModel.deleteNote(note)
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = "Delete",
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            DetailSectionCard(title = "Debug Checklist & Tasks") {
                                ChecklistComponent(
                                    items = checklist,
                                    onToggleItem = { item, isComp -> viewModel.toggleChecklistItem(item, isComp) },
                                    onAddItem = { text -> viewModel.addChecklistItem(text) },
                                    onDeleteItem = { item -> viewModel.deleteChecklistItem(item) }
                                )
                            }
                        }
                    }

                    3 -> {
                        // Root Cause & Fix (Αἰτία) Tab
                        item {
                            DetailSectionCard(title = "Suspected Cause — Root Cause (Αἰτία)") {
                                OutlinedTextField(
                                    value = issue.suspectedCause,
                                    onValueChange = { newCause ->
                                        viewModel.updateIssueField { it.copy(suspectedCause = newCause) }
                                    },
                                    placeholder = { Text("Why did this issue happen? (e.g. Permission callback unhandled)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AitiaPurple,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        item {
                            DetailSectionCard(
                                title = "Fix / Solution",
                                headerAction = {
                                    TextButton(onClick = { showGitCommitDialog = true }) {
                                        Text("🌿 Git Commit Msg", style = MaterialTheme.typography.labelSmall, color = Color(0xFF58A6FF))
                                    }
                                }
                            ) {
                                OutlinedTextField(
                                    value = issue.solution,
                                    onValueChange = { newFix ->
                                        viewModel.updateIssueField { it.copy(solution = newFix) }
                                    },
                                    placeholder = { Text("What changes were made to fix the defect?") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = extendedColors.statusFixed,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        item {
                            DetailSectionCard(title = "Verification Notes") {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = issue.verification,
                                        onValueChange = { newVer ->
                                            viewModel.updateIssueField { it.copy(verification = newVer) }
                                        },
                                        placeholder = { Text("Retested on Pixel 8 / Android 15. Crash no longer reproduces.") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = extendedColors.statusVerified,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    if (issue.status != IssueStatus.VERIFIED) {
                                        Button(
                                            onClick = {
                                                haptic.success()
                                                viewModel.updateStatus(IssueStatus.VERIFIED)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = extendedColors.statusVerified),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Mark as Verified", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Cross-Device Verification Matrix
                        item {
                            CrossDeviceVerificationMatrix()
                        }
                    }

                    4 -> {
                        // History & Relations Tab
                        // Attachments Section
                        item {
                            DetailSectionCard(
                                title = "Attachments & Evidence",
                                headerAction = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        TextButton(onClick = { showCameraCaptureDialog = true }) {
                                            Text("📸 Camera", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00FF88))
                                        }
                                        TextButton(onClick = { showAudioRecorderDialog = true }) {
                                            Text("🎙️ Audio", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00F0FF))
                                        }
                                        TextButton(onClick = {
                                            photoPickerLauncher.launch(
                                                androidx.activity.result.PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                                )
                                            )
                                        }) {
                                            Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("+ Photo", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            ) {
                                // Action Chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { showBarcodeScannerDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("🏷️ Scan Tag", fontSize = 11.sp, color = Color.White)
                                    }

                                    Button(
                                        onClick = { showBugCardShareDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("🎟️ Export Card", fontSize = 11.sp, color = Color(0xFF00FF88))
                                    }

                                    Button(
                                        onClick = { filePickerLauncher.launch("*/*") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("+ File", fontSize = 11.sp, color = Color.White)
                                    }
                                }

                                if (attachments.isEmpty()) {
                                    Text(
                                        text = "No screenshots, videos, or logs attached yet. Use in-app camera or file picker.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        attachments.forEach { att ->
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { viewingAttachment = att },
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = if (att.isImage) Icons.Default.Image else Icons.Default.AttachFile,
                                                            contentDescription = null,
                                                            tint = AitiaBlue,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(
                                                                text = att.filename,
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurface,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                            Text(
                                                                text = "${att.mimeType} · ${DateFormatter.formatRelativeTime(att.createdAt)}",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    }

                                                    Row {
                                                        if (att.isImage) {
                                                            TextButton(onClick = {
                                                                val file = File(att.uriPath.removePrefix("file://"))
                                                                regressionActualImageFile = file
                                                                showVisualRegressionDialog = true
                                                            }) {
                                                                Text("🔀 Compare", fontSize = 11.sp, color = Color(0xFF00F0FF))
                                                            }
                                                        }

                                                        IconButton(
                                                            onClick = {
                                                                haptic.lightTap()
                                                                viewModel.deleteAttachment(att)
                                                            },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Delete,
                                                                contentDescription = "Delete",
                                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Related Issues Section
                        item {
                            DetailSectionCard(
                                title = "Related Issues",
                                headerAction = {
                                    TextButton(onClick = { showAddRelationDialog = true }) {
                                        Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Link", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            ) {
                                if (relatedIssues.isEmpty()) {
                                    Text(
                                        text = "No related issues linked.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        relatedIssues.forEach { rel ->
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = rel.relationshipType.displayName,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = AitiaBlue,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "#${rel.targetIssueId} — ${rel.targetTitle}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Audit Timeline
                        item {
                            DetailSectionCard(title = "Audit Timeline") {
                                if (timeline.isEmpty()) {
                                    Text(
                                        text = "No timeline events recorded yet.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        timeline.forEach { event ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(AitiaBlue)
                                                        .padding(top = 4.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = event.title,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "· ${DateFormatter.formatRelativeTime(event.timestamp)}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    if (event.description.isNotBlank()) {
                                                        Text(
                                                            text = event.description,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // 1. In-App Camera with Live Bug Markup Dialog
        if (showCameraCaptureDialog) {
            AitiaCameraCaptureDialog(
                onDismiss = { showCameraCaptureDialog = false },
                onImageCaptured = { capturedFile ->
                    viewModel.addAttachment(
                        filename = capturedFile.name,
                        uriPath = capturedFile.absolutePath,
                        mimeType = "image/png",
                        sizeBytes = capturedFile.length()
                    )
                }
            )
        }

        // 2. Scan-to-StackTrace OCR Dialog
        if (showScanOcrDialog) {
            ScanStackTraceDialog(
                onDismiss = { showScanOcrDialog = false },
                onApplyParsedStackTrace = { parsed, raw ->
                    viewModel.updateIssueField { current ->
                        current.copy(
                            exceptionType = parsed.exceptionType ?: current.exceptionType,
                            errorMessage = parsed.errorMessage ?: current.errorMessage,
                            sourceFile = parsed.sourceFile ?: current.sourceFile,
                            sourceLine = parsed.sourceLine ?: current.sourceLine,
                            technicalDetails = if (current.technicalDetails.isBlank()) raw else "${current.technicalDetails}\n\n// --- Scanned Terminal Logs ---\n$raw"
                        )
                    }
                }
            )
        }

        // 3. Visual Regression Compare Dialog
        if (showVisualRegressionDialog && regressionActualImageFile != null) {
            VisualRegressionCompareDialog(
                actualImageFile = regressionActualImageFile!!,
                onDismiss = { showVisualRegressionDialog = false }
            )
        }

        // 5. Voice Speech-to-Steps Dialog
        if (showVoiceStepsDialog) {
            VoiceReproStepsDialog(
                initialText = issue.stepsToReproduce,
                onDismiss = { showVoiceStepsDialog = false },
                onApplySteps = { steps ->
                    viewModel.updateIssueField { it.copy(stepsToReproduce = steps) }
                }
            )
        }

        // 6. Audio Glitch Recorder Dialog
        if (showAudioRecorderDialog) {
            AudioGlitchRecorderDialog(
                onDismiss = { showAudioRecorderDialog = false },
                onAudioRecorded = { audioFile ->
                    viewModel.addAttachment(
                        filename = audioFile.name,
                        uriPath = audioFile.absolutePath,
                        mimeType = "audio/mp4",
                        sizeBytes = audioFile.length()
                    )
                }
            )
        }

        // 9. cURL / Network Inspector Dialog
        if (showCurlInspectorDialog) {
            NetworkCurlInspectorDialog(
                onDismiss = { showCurlInspectorDialog = false },
                onAttachToIssue = { curl, formattedJson ->
                    val payload = "// --- cURL Command ---\n$curl\n\n// --- Formatted JSON ---\n$formattedJson"
                    val currentTech = issue.technicalDetails
                    val newTech = if (currentTech.isBlank()) payload else "$currentTech\n\n$payload"
                    viewModel.updateIssueField { it.copy(technicalDetails = newTech) }
                }
            )
        }

        // 12. Git Commit Dialog
        if (showGitCommitDialog) {
            GitCommitDialog(
                issue = issue,
                onDismiss = { showGitCommitDialog = false }
            )
        }

        // 14. Barcode / Asset Tag Scanner Dialog
        if (showBarcodeScannerDialog) {
            BarcodeScannerDialog(
                onDismiss = { showBarcodeScannerDialog = false },
                onBarcodeScanned = { scanned ->
                    viewModel.addTag("asset:$scanned")
                }
            )
        }

        // 17. Bug Card Share Dialog
        if (showBugCardShareDialog) {
            BugCardShareDialog(
                issue = issue,
                onDismiss = { showBugCardShareDialog = false }
            )
        }

        // Add Tag Dialog with autocomplete
        if (showAddTagDialog) {
            var tagInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddTagDialog = false },
                title = { Text("Add Tag") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tagInput,
                            onValueChange = { tagInput = it },
                            placeholder = { Text("e.g. camera, android-15, login") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        if (allTags.isNotEmpty()) {
                            Text("Existing Tags:", style = MaterialTheme.typography.labelSmall)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                allTags.take(8).forEach { t ->
                                    FilterChip(
                                        selected = false,
                                        onClick = { tagInput = t.name },
                                        label = { Text("#${t.name}", style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tagInput.isNotBlank()) {
                                viewModel.addTag(tagInput)
                                showAddTagDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTagDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Resolution Workflow prompt
        if (showResolutionPrompt) {
            var fixNotes by remember { mutableStateOf(issue.solution) }
            var verifyNotes by remember { mutableStateOf(issue.verification) }
            AlertDialog(
                onDismissRequest = { showResolutionPrompt = false },
                title = { Text("Record Resolution Details") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Preserve what changed and how it was verified for future reference.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = fixNotes,
                            onValueChange = { fixNotes = it },
                            label = { Text("Fix Summary") },
                            placeholder = { Text("What code/config changed?") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = verifyNotes,
                            onValueChange = { verifyNotes = it },
                            label = { Text("Verification Notes") },
                            placeholder = { Text("Tested on Pixel 8 / Android 15. Verified fixed.") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateIssueField { it.copy(solution = fixNotes, verification = verifyNotes) }
                            showResolutionPrompt = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = extendedColors.statusFixed)
                    ) {
                        Text("Save Resolution")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResolutionPrompt = false }) {
                        Text("Later")
                    }
                }
            )
        }

        // Attachment Viewer Dialog
        if (viewingAttachment != null) {
            val att = viewingAttachment!!
            Dialog(onDismissRequest = { viewingAttachment = null }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = att.filename,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { viewingAttachment = null }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (att.isImage) Icons.Default.Image else Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = AitiaBlue,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "URI: ${att.uriPath}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Add Note Dialog
        if (showAddNoteDialog) {
            var noteInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddNoteDialog = false },
                title = { Text("Add Investigation Note") },
                text = {
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = { Text("e.g. 10:32 — Confirmed crash only occurs on Android 15.") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (noteInput.isNotBlank()) {
                                viewModel.addNote(noteInput)
                                showAddNoteDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Add Note")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddNoteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Add Relation Dialog
        if (showAddRelationDialog) {
            var selectedTargetId by remember { mutableStateOf<Long?>(null) }
            var selectedRelType by remember { mutableStateOf(RelationshipType.RELATED_TO) }

            AlertDialog(
                onDismissRequest = { showAddRelationDialog = false },
                title = { Text("Link Related Issue") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Relationship Type", style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            RelationshipType.entries.take(3).forEach { rel ->
                                FilterChip(
                                    selected = selectedRelType == rel,
                                    onClick = { selectedRelType = rel },
                                    label = { Text(rel.displayName, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select Target Issue", style = MaterialTheme.typography.labelSmall)
                        uiState.allIssues.take(5).forEach { candidate ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (selectedTargetId == candidate.id) AitiaBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedTargetId = candidate.id }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${candidate.id} — ${candidate.title}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedTargetId != null) {
                                viewModel.linkRelatedIssue(selectedTargetId!!, selectedRelType)
                                showAddRelationDialog = false
                            }
                        },
                        enabled = selectedTargetId != null,
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Link")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddRelationDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Issue #${issue.id}?") },
                text = { Text("Are you sure you want to permanently delete this issue and all attached notes and logs? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteIssue {
                                showDeleteDialog = false
                                onNavigateBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = extendedColors.priorityCritical)
                    ) {
                        Text("Delete Permanently")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    headerAction: (@Composable () -> Unit)? = null,
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
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                headerAction?.invoke()
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
