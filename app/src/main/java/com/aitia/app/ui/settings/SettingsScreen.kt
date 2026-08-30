package com.aitia.app.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val prefs = uiState.preferences
    val haptic = rememberHapticFeedback()
    val extendedColors = LocalExtendedColors.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val jsonContent = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader -> reader.readText() }
                if (!jsonContent.isNullOrBlank()) {
                    viewModel.importBackupJson(jsonContent) {}
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
                SettingsSectionCard(title = "Appearance & OLED Themes") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Developer-tailored visual themes optimized for 120Hz OLED displays & dark room debugging.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppThemeMode.entries.forEach { mode ->
                                val isSelected = prefs.themeMode == mode
                                val primaryColor = Color(android.graphics.Color.parseColor(mode.primaryColorHex))
                                val secondaryColor = Color(android.graphics.Color.parseColor(mode.secondaryColorHex))
                                val surfaceColor = Color(android.graphics.Color.parseColor(mode.surfaceColorHex))

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            haptic.success()
                                            viewModel.setThemeMode(mode)
                                        },
                                    color = if (isSelected) primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                    tonalElevation = if (isSelected) 2.dp else 0.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = mode.displayName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (mode.isOled) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        color = Color(0xFF00FF88).copy(alpha = 0.15f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "OLED 0%",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = Color(0xFF00FF88),
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = mode.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Color Swatches
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(primaryColor)
                                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(secondaryColor)
                                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(surfaceColor)
                                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. API Keys & Integrations
            item {
                SettingsSectionCard(title = "API Keys & Integrations") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Provide your own API keys to enable AI debugging and GitHub syncing. Stored securely on-device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        OutlinedTextField(
                            value = uiState.geminiApiKey,
                            onValueChange = { viewModel.setGeminiApiKey(it) },
                            label = { Text("Gemini API Key") },
                            placeholder = { Text("AI Studio Key (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = uiState.githubPat,
                            onValueChange = { viewModel.setGithubPat(it) },
                            label = { Text("GitHub Personal Access Token (PAT)") },
                            placeholder = { Text("ghp_...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = uiState.defaultRepo,
                            onValueChange = { viewModel.setDefaultRepo(it) },
                            label = { Text("Default GitHub Repository") },
                            placeholder = { Text("owner/repo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // 3. Security & App Lock
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

            // 3. QA Sensor Tools & Quick Capture
            item {
                SettingsSectionCard(title = "QA Sensors & Fast Capture") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Shake to Report Bug",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Firmly shake physical device to trigger Quick Capture popup instantly while testing",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = prefs.isShakeToReportEnabled,
                                onCheckedChange = { isChecked ->
                                    haptic.lightTap()
                                    viewModel.setShakeToReport(isChecked)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00FF88))
                            )
                        }
                    }
                }
            }

            // 4. Backup & Export
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
                            onClick = { importLauncher.launch("application/json") },
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
                        ) {
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
