package com.aitia.app.ui.detail

import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.aitia.app.ui.components.AiDebugAssistantDialog
import com.aitia.app.ui.components.AitiaCameraCaptureDialog
import com.aitia.app.ui.components.AudioGlitchRecorderDialog
import com.aitia.app.ui.components.BarcodeScannerDialog
import com.aitia.app.ui.components.BugCardShareDialog
import com.aitia.app.ui.components.ChecklistComponent
import com.aitia.app.ui.components.CodeViewer
import com.aitia.app.ui.components.ConfettiEffect
import com.aitia.app.ui.components.CrossDeviceVerificationMatrix
import com.aitia.app.ui.components.DeviceVitalsCard
import com.aitia.app.ui.components.EmptyStateView
import com.aitia.app.ui.components.FeatureTourDialog
import com.aitia.app.ui.components.GitCommitDialog
import com.aitia.app.ui.components.GitHubPrSyncDialog
import com.aitia.app.ui.components.MagicToolkitBottomSheet
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
import com.aitia.app.ui.components.WirelessAdbDialog
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
    var showConfetti by remember { mutableStateOf(false) }
    var showMagicToolkit by remember { mutableStateOf(false) }
    var showTourDialog by remember { mutableStateOf(false) }
    var showAiAssistantDialog by remember { mutableStateOf(false) }
    var showGitHubPrDialog by remember { mutableStateOf(false) }
    var showWirelessAdbDialog by remember { mutableStateOf(false) }

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

    // Streamlined 3-Tab Structure: Simple, Intuitive, Friendly
    val tabs = listOf("Overview & Fix", "Diagnostics & Specs", "Activity & Notes")

    if (issue == null) {
        com.aitia.app.ui.components.AitiaLoadingScreen(
            message = "Loading defect details...",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val matchedFixes = remember(issue, uiState.allIssues) {
        PreviousFixMatcher.findSimilarResolvedFixes(issue, uiState.allIssues)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        // Help / How to use
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
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        haptic.lightTap()
                        showMagicToolkit = true
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },
                    text = {
                        Text(
                            text = "Magic Toolkit ✨",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    containerColor = Color(0xFF00E5FF),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
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
                // 1. Status Progress Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STATUS PROGRESS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            if (issue.isResolved) {
                                Text(
                                    text = "🎉 Bug Resolved!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF00FF88),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusFlowBar(
                            currentStatus = issue.status,
                            onStatusSelected = { newStatus ->
                                viewModel.updateStatus(newStatus)
                                if (newStatus == IssueStatus.FIXED || newStatus == IssueStatus.VERIFIED) {
                                    haptic.success()
                                    showConfetti = true
                                    showResolutionPrompt = true
                                    Toast.makeText(context, "🎉 Bug Squashed! Great job!", Toast.LENGTH_SHORT).show()
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
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "#${tag.name}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AitiaBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = AitiaBlue,
                                            modifier = Modifier.size(12.dp)
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

                // 3. Tab Row (3 Streamlined, Clean Tabs)
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
                            // TAB 0: OVERVIEW & FIX

                            // 1. Previous Fix Memory Matcher Banner (if similar issue was resolved before)
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

                            // 2. Smart Fix Advisor (ELI5 Plain English + Developer Code)
                            item {
                                RootCauseDiagnosisCard(
                                    exceptionType = issue.exceptionType,
                                    errorMessage = issue.errorMessage.ifBlank { issue.technicalDetails },
                                    onApplySuggestedFix = { codeSnippet ->
                                        viewModel.updateIssueField { it.copy(solution = codeSnippet) }
                                    }
                                )
                            }

                            // AI Triage & PR Sync Action Row
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            haptic.lightTap()
                                            showAiAssistantDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("🤖 Ask Local AI Assistant", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            haptic.lightTap()
                                            showGitHubPrDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("🐙 PR Sync", color = Color(0xFFBC8CFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }

                            // 3. Description
                            item {
                                DetailSectionCard(title = "Description") {
                                    OutlinedTextField(
                                        value = issue.description,
                                        onValueChange = { newDesc ->
                                            viewModel.updateIssueField { it.copy(description = newDesc) }
                                        },
                                        placeholder = { Text("What went wrong? Describe what happened...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AitiaBlue,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }

                            // 4. Screen / Feature Area
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

                            // 5. Steps to Reproduce (With Voice Dictation trigger)
                            item {
                                DetailSectionCard(
                                    title = "Steps to Reproduce",
                                    headerAction = {
                                        TextButton(onClick = { showVoiceStepsDialog = true }) {
                                            Text("🎙️ Speak Steps", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
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

                            // 6. Interactive Task Checklist
                            item {
                                DetailSectionCard(title = "Investigation Checklist") {
                                    ChecklistComponent(
                                        items = checklist,
                                        onAddItem = { text -> viewModel.addChecklistItem(text) },
                                        onToggleItem = { item, isCompleted -> viewModel.toggleChecklistItem(item, isCompleted) },
                                        onDeleteItem = { item -> viewModel.deleteChecklistItem(item) }
                                    )
                                }
                            }

                            // 7. Fix, Root Cause & Solution
                            item {
                                DetailSectionCard(
                                    title = "Root Cause & Fix (Αἰτία)",
                                    headerAction = {
                                        TextButton(onClick = { showGitCommitDialog = true }) {
                                            Text("🌿 Git Commit Msg", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = issue.suspectedCause,
                                            onValueChange = { newCause ->
                                                viewModel.updateIssueField { it.copy(suspectedCause = newCause) }
                                            },
                                            label = { Text("Why did it happen?") },
                                            placeholder = { Text("Root cause explanation...") },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AitiaPurple,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        OutlinedTextField(
                                            value = issue.solution,
                                            onValueChange = { newSol ->
                                                viewModel.updateIssueField { it.copy(solution = newSol) }
                                            },
                                            label = { Text("How was it fixed?") },
                                            placeholder = { Text("Code change details or pattern...") },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = StatusFixed,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        OutlinedTextField(
                                            value = issue.verification,
                                            onValueChange = { newVer ->
                                                viewModel.updateIssueField { it.copy(verification = newVer) }
                                            },
                                            label = { Text("Verification Notes") },
                                            placeholder = { Text("Tested on Pixel 8 (Android 15), verified fix...") },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = StatusFixed,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            // TAB 1: DIAGNOSTICS & SPECS
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Fast Diagnostic Action Bar
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { showScanOcrDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("📸 Scan Screen OCR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                haptic.success()
                                                viewModel.harvestLogcatToTechnicalDetails()
                                                Toast.makeText(context, "Recent crash logs captured!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("📋 Grab Crash Logs", color = Color.White, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                haptic.success()
                                                viewModel.appendDeviceVitals(context)
                                                Toast.makeText(context, "Hardware specs attached!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("⚡ Snapshot Specs", color = Color.White, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { showCurlInspectorDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("🌐 cURL / Network", color = Color(0xFF00FF88), fontSize = 12.sp)
                                        }
                                    }

                                    // Extracted Diagnostics Card
                                    DetailSectionCard(title = "Detected Error Diagnostics") {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = "Exception: ",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = issue.exceptionType.ifBlank { "None detected" },
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
                                                    text = if (issue.sourceFile.isNotBlank()) "${issue.sourceFile}:${issue.sourceLine}" else "None detected",
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

                                    // StackTrace & Raw Logs CodeViewer
                                    CodeViewer(
                                        codeText = issue.technicalDetails,
                                        onCodeChange = { newLogs ->
                                            viewModel.updateIssueField { it.copy(technicalDetails = newLogs) }
                                        },
                                        onParseStackTrace = {
                                            haptic.success()
                                            viewModel.parseLogsAndAutoPopulate()
                                            Toast.makeText(context, "Stack trace parsed!", Toast.LENGTH_SHORT).show()
                                        }
                                    )

                                    // Live Hardware & System Vitals Card
                                    DeviceVitalsCard(
                                        onSnapshotCaptured = { snapshot ->
                                            viewModel.appendDeviceVitals(context)
                                            Toast.makeText(context, "Device specs saved to issue", Toast.LENGTH_SHORT).show()
                                        }
                                    )

                                    // Multi-Device QA Verification Matrix
                                    CrossDeviceVerificationMatrix(
                                        onMatrixUpdated = { updatedList ->
                                            val summary = updatedList.joinToString("\n") { "${it.deviceName} (${it.osVersion}): ${it.status.label}" }
                                            viewModel.updateIssueField { it.copy(verification = summary) }
                                        }
                                    )
                                }
                            }
                        }

                        2 -> {
                            // TAB 2: ACTIVITY & NOTES
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Quick Attachment Action Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { showCameraCaptureDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("📸 Snap & Draw", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { showAudioRecorderDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF70A6)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("🎙️ Audio Memo", color = Color.White, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                val existingImage = attachments.firstOrNull { it.isImage }
                                                if (existingImage != null) {
                                                    regressionActualImageFile = File(existingImage.uriPath.removePrefix("file://"))
                                                }
                                                showVisualRegressionDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("🔀 Compare Design", color = Color(0xFFFFB703), fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { showBarcodeScannerDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("🏷️ Scan Tag", color = Color.White, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    androidx.activity.result.PickVisualMediaRequest(
                                                        ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                                    )
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("📁 Add File", color = Color.White, fontSize = 12.sp)
                                        }
                                    }

                                    // Attachments Section
                                    DetailSectionCard(
                                        title = "Media & Attachments (${attachments.size})",
                                        headerAction = {
                                            TextButton(onClick = { showCameraCaptureDialog = true }) {
                                                Text("+ Snap Photo", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00E5FF))
                                            }
                                        }
                                    ) {
                                        if (attachments.isEmpty()) {
                                            Text(
                                                text = "No photos, audio recordings, or videos attached yet.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                attachments.forEach { attachment ->
                                                    Surface(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .clickable { viewingAttachment = attachment },
                                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(10.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Icon(
                                                                    imageVector = when {
                                                                        attachment.isImage -> Icons.Default.CameraAlt
                                                                        attachment.isVideo -> Icons.Default.CameraAlt
                                                                        attachment.isAudio -> Icons.Default.Mic
                                                                        else -> Icons.Default.Folder
                                                                    },
                                                                    contentDescription = null,
                                                                    tint = AitiaBlue,
                                                                    modifier = Modifier.size(20.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Column {
                                                                    Text(
                                                                        text = attachment.filename,
                                                                        style = MaterialTheme.typography.bodyMedium,
                                                                        fontWeight = FontWeight.Medium,
                                                                        maxLines = 1
                                                                    )
                                                                    Text(
                                                                        text = "${attachment.formattedSize} • ${DateFormatter.formatAbsolute(attachment.createdAt)}",
                                                                        style = MaterialTheme.typography.labelSmall,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                }
                                                            }

                                                            IconButton(
                                                                onClick = { viewModel.deleteAttachment(attachment) },
                                                                modifier = Modifier.size(28.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Close,
                                                                    contentDescription = "Delete",
                                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Debugging Notes Journal
                                    DetailSectionCard(
                                        title = "Investigation Journal (${notes.size})",
                                        headerAction = {
                                            Row {
                                                TextButton(onClick = { showVoiceStepsDialog = true }) {
                                                    Text("🎙️ Dictate", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00E5FF))
                                                }
                                                TextButton(onClick = { showAddNoteDialog = true }) {
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
                                                                    imageVector = Icons.Default.Close,
                                                                    contentDescription = "Delete",
                                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Timeline Events
                                    DetailSectionCard(title = "Change History (${timeline.size})") {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            timeline.forEach { event ->
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(AitiaBlue)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "${event.description} • ${DateFormatter.formatAbsolute(event.timestamp)}",
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

                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }

        // Celebratory Confetti Animation when Bug is Squashed!
        ConfettiEffect(
            trigger = showConfetti,
            onAnimationEnd = { showConfetti = false }
        )
    }

    // Modal Superpower Dialogs
    if (showMagicToolkit) {
        MagicToolkitBottomSheet(
            onDismiss = { showMagicToolkit = false },
            onLaunchAiAssistant = { showAiAssistantDialog = true },
            onLaunchGitHubPr = { showGitHubPrDialog = true },
            onLaunchWirelessAdb = { showWirelessAdbDialog = true },
            onLaunchCameraMarkup = { showCameraCaptureDialog = true },
            onLaunchOcrScan = { showScanOcrDialog = true },
            onLaunchVoiceSteps = { showVoiceStepsDialog = true },
            onLaunchAudioGlitch = { showAudioRecorderDialog = true },
            onLaunchVisualCompare = {
                val existingImage = attachments.firstOrNull { it.isImage }
                if (existingImage != null) {
                    regressionActualImageFile = File(existingImage.uriPath.removePrefix("file://"))
                }
                showVisualRegressionDialog = true
            },
            onSnapshotVitals = {
                viewModel.appendDeviceVitals(context)
                Toast.makeText(context, "Hardware specs attached!", Toast.LENGTH_SHORT).show()
            },
            onDumpLogcat = {
                viewModel.harvestLogcatToTechnicalDetails()
                Toast.makeText(context, "Recent crash logs captured!", Toast.LENGTH_SHORT).show()
            },
            onLaunchCurlInspector = { showCurlInspectorDialog = true },
            onLaunchShareBugCard = { showBugCardShareDialog = true },
            onLaunchBarcodeScanner = { showBarcodeScannerDialog = true }
        )
    }

    if (showAiAssistantDialog) {
        AiDebugAssistantDialog(
            issue = issue,
            onDismiss = { showAiAssistantDialog = false },
            onApplySolution = { generatedFix ->
                showAiAssistantDialog = false
                viewModel.updateIssueField { it.copy(solution = generatedFix) }
            }
        )
    }

    if (showGitHubPrDialog) {
        GitHubPrSyncDialog(
            issue = issue,
            onDismiss = { showGitHubPrDialog = false }
        )
    }

    if (showWirelessAdbDialog) {
        WirelessAdbDialog(
            onDismiss = { showWirelessAdbDialog = false }
        )
    }

    if (showTourDialog) {
        FeatureTourDialog(onDismiss = { showTourDialog = false })
    }

    if (showCameraCaptureDialog) {
        AitiaCameraCaptureDialog(
            onDismiss = { showCameraCaptureDialog = false },
            onImageCaptured = { savedFile ->
                showCameraCaptureDialog = false
                viewModel.addAttachment(
                    filename = savedFile.name,
                    uriPath = Uri.fromFile(savedFile).toString(),
                    mimeType = "image/png",
                    sizeBytes = savedFile.length()
                )
                Toast.makeText(context, "Annotated image saved!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showScanOcrDialog) {
        ScanStackTraceDialog(
            onDismiss = { showScanOcrDialog = false },
            onApplyParsedStackTrace = { parsedResult, fullRawText ->
                showScanOcrDialog = false
                val current = viewModel.uiState.value.issue
                if (current != null) {
                    viewModel.updateIssueField {
                        it.copy(
                            technicalDetails = if (it.technicalDetails.isBlank()) fullRawText else "${it.technicalDetails}\n\n$fullRawText",
                            exceptionType = parsedResult.exceptionType ?: it.exceptionType,
                            errorMessage = parsedResult.errorMessage ?: it.errorMessage,
                            sourceFile = parsedResult.sourceFile ?: it.sourceFile,
                            sourceLine = parsedResult.sourceLine ?: it.sourceLine
                        )
                    }
                }
                Toast.makeText(context, "Error parsed and populated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showVisualRegressionDialog) {
        val imageFile = regressionActualImageFile
        if (imageFile != null && imageFile.exists()) {
            VisualRegressionCompareDialog(
                actualImageFile = imageFile,
                onDismiss = { showVisualRegressionDialog = false }
            )
        } else {
            Toast.makeText(context, "Please attach or take a bug screenshot first to compare against design.", Toast.LENGTH_LONG).show()
            showVisualRegressionDialog = false
        }
    }

    if (showVoiceStepsDialog) {
        VoiceReproStepsDialog(
            onDismiss = { showVoiceStepsDialog = false },
            onApplySteps = { formattedSteps ->
                showVoiceStepsDialog = false
                viewModel.updateIssueField { current ->
                    val combined = if (current.stepsToReproduce.isBlank()) formattedSteps else "${current.stepsToReproduce}\n$formattedSteps"
                    current.copy(stepsToReproduce = combined)
                }
                Toast.makeText(context, "Voice steps appended!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAudioRecorderDialog) {
        AudioGlitchRecorderDialog(
            onDismiss = { showAudioRecorderDialog = false },
            onAudioRecorded = { audioFile ->
                showAudioRecorderDialog = false
                viewModel.addAttachment(
                    filename = audioFile.name,
                    uriPath = Uri.fromFile(audioFile).toString(),
                    mimeType = "audio/mp4",
                    sizeBytes = audioFile.length()
                )
                Toast.makeText(context, "Audio memo saved!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showCurlInspectorDialog) {
        NetworkCurlInspectorDialog(
            onDismiss = { showCurlInspectorDialog = false },
            onAttachToIssue = { curlCmd, formattedJson ->
                showCurlInspectorDialog = false
                viewModel.updateIssueField { current ->
                    val payload = "\n\n### Network cURL Command\n```bash\n$curlCmd\n```\n\n```json\n$formattedJson\n```"
                    current.copy(technicalDetails = current.technicalDetails + payload)
                }
                Toast.makeText(context, "cURL payload attached!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showGitCommitDialog) {
        GitCommitDialog(
            issue = issue,
            onDismiss = { showGitCommitDialog = false }
        )
    }

    if (showBarcodeScannerDialog) {
        BarcodeScannerDialog(
            onDismiss = { showBarcodeScannerDialog = false },
            onBarcodeScanned = { rawBarcode ->
                showBarcodeScannerDialog = false
                viewModel.addTag("asset:$rawBarcode")
                Toast.makeText(context, "Asset tag linked: $rawBarcode", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showBugCardShareDialog) {
        BugCardShareDialog(
            issue = issue,
            onDismiss = { showBugCardShareDialog = false }
        )
    }

    // Add Note Dialog
    if (showAddNoteDialog) {
        var noteInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Add Debugging Note") },
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

    // Add Tag Dialog
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
                        placeholder = { Text("e.g. ui, network, auth") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (allTags.isNotEmpty()) {
                        Text("Existing tags:", style = MaterialTheme.typography.labelSmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            allTags.take(8).forEach { tag ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
                                            viewModel.addTag(tag.name)
                                            showAddTagDialog = false
                                        },
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "#${tag.name}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tagInput.isNotBlank()) {
                            viewModel.addTag(tagInput.trim().removePrefix("#"))
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
