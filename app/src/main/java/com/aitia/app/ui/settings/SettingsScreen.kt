package com.aitia.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aitia.app.domain.model.AppThemeMode
import com.aitia.app.domain.model.Priority
import com.aitia.app.ui.components.AitiaTopAppBar
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.ui.theme.LocalExtendedColors
import com.aitia.app.ui.theme.StatusFixed
import com.aitia.app.util.ShareHelper
import com.aitia.app.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val prefs = uiState.preferences
    val haptic = rememberHapticFeedback()
    val extendedColors = LocalExtendedColors.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Preferences",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Appearance & Theme
            item {
                SettingsSectionCard(title = "Appearance") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Theme Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AppThemeMode.entries.forEach { mode ->
                                FilterChip(
                                    selected = prefs.themeMode == mode,
                                    onClick = {
                                        haptic.lightTap()
                                        viewModel.setThemeMode(mode)
                                    },
                                    label = { Text(mode.name, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Security & App Lock
            item {
                SettingsSectionCard(title = "Security & Privacy") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "App Lock Protection",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Protect logs, notes, and stack traces on launch",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = prefs.isAppLockEnabled,
                                onCheckedChange = { isChecked ->
                                    haptic.lightTap()
                                    if (isChecked && prefs.appLockPin.isEmpty()) {
                                        showPinSetupDialog = true
                                    } else {
                                        viewModel.setAppLock(isChecked, prefs.appLockPin, prefs.isBiometricEnabled)
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = AitiaBlue)
                            )
                        }

                        if (prefs.isAppLockEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Change PIN (${prefs.appLockPin.length} digits)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AitiaBlue,
                                    modifier = Modifier.clickable { showPinSetupDialog = true }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Backup & Export
            item {
                SettingsSectionCard(title = "Data Backup & Export") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Export JSON
                        OutlinedButton(
                            onClick = {
                                haptic.lightTap()
                                viewModel.exportBackupJson { file ->
                                    ShareHelper.shareFile(context, file, "application/json", "Export Aitia Backup")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Full JSON Backup")
                        }

                        // Export Markdown Report
                        OutlinedButton(
                            onClick = {
                                haptic.lightTap()
                                viewModel.exportMarkdown { file ->
                                    ShareHelper.shareFile(context, file, "text/markdown", "Export Markdown Report")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Markdown Defect Report")
                        }

                        // Export CSV
                        OutlinedButton(
                            onClick = {
                                haptic.lightTap()
                                viewModel.exportCsv { file ->
                                    ShareHelper.shareFile(context, file, "text/csv", "Export CSV Spreadsheet")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export CSV Spreadsheet")
                        }

                        // Import JSON
                        Button(
                            onClick = { showImportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import JSON Backup", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // 4. Sample Data & Reset
            item {
                SettingsSectionCard(title = "Developer Demo Data") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                haptic.success()
                                viewModel.seedSampleData {
                                    onNavigateBack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                            if (uiState.isSeeding) {
                                Text("Populating demo environment...")
                            } else {
                                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Seed Realistic Sample Data")
                            }
                        }

                        if (uiState.isSeeding) {
                            androidx.compose.ui.window.Dialog(onDismissRequest = {}) {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp)),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 6.dp
                                ) {
                                    com.aitia.app.ui.components.AitiaLoadingScreen(
                                        message = "Seeding developer projects & issues...",
                                        modifier = Modifier.padding(32.dp)
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showClearDataDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = extendedColors.priorityCritical),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear All Local Data")
                        }
                    }
                }
            }

            // 5. About Aitia (Αἰτία)
            item {
                SettingsSectionCard(title = "About Aitia") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Aitia (Αἰτία) — v1.0.0",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "From Greek Αἰτία (cause / reason). Built for software engineers, solo developers, and QA professionals to capture what went wrong, understand why it happened, and record how it was fixed.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "100% Offline-First & Privacy-First. All your code, logs, and bug notes remain securely on your device.",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusFixed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Setup PIN Dialog
        if (showPinSetupDialog) {
            var pinInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showPinSetupDialog = false },
                title = { Text("Set App Lock PIN") },
                text = {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 6) pinInput = it },
                        label = { Text("4-6 Digit PIN") },
                        placeholder = { Text("1234") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (pinInput.length >= 4) {
                                viewModel.setAppLock(true, pinInput, true)
                                showPinSetupDialog = false
                            }
                        },
                        enabled = pinInput.length >= 4,
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Save PIN")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinSetupDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Import JSON Dialog
        if (showImportDialog) {
            var jsonInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Paste JSON Backup Content") },
                text = {
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { jsonInput = it },
                        placeholder = { Text("Paste backup JSON here...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 8
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (jsonInput.isNotBlank()) {
                                viewModel.importBackupJson(jsonInput) {
                                    showImportDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AitiaBlue)
                    ) {
                        Text("Import")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Clear All Data Dialog
        if (showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { showClearDataDialog = false },
                title = { Text("Clear All Data?") },
                text = { Text("This will permanently delete all projects, issues, testing sessions, and notes. Make sure to export a backup first if needed.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllData {
                                showClearDataDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = extendedColors.priorityCritical)
                    ) {
                        Text("Clear Everything")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
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
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
