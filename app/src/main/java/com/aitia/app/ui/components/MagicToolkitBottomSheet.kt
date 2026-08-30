package com.aitia.app.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicToolkitBottomSheet(
    onDismiss: () -> Unit,
    onLaunchCameraMarkup: () -> Unit,
    onLaunchOcrScan: () -> Unit,
    onLaunchVoiceSteps: () -> Unit,
    onLaunchAudioGlitch: () -> Unit,
    onLaunchVisualCompare: () -> Unit,
    onSnapshotVitals: () -> Unit,
    onDumpLogcat: () -> Unit,
    onLaunchCurlInspector: () -> Unit,
    onLaunchShareBugCard: () -> Unit,
    onLaunchBarcodeScanner: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val haptic = rememberHapticFeedback()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0D1117)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Magic Toolkit ✨",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tap any tool to capture, scan, or diagnose effortlessly",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8B949E)
                    )
                }
            }

            Text(
                text = "INSTANT CAPTURE & VISION",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8B949E),
                fontWeight = FontWeight.Bold
            )

            // 1. Camera + Markup
            MagicToolCard(
                title = "📸 Snap & Draw on Screen",
                subtitle = "Take a photo & circle the bug with arrows, boxes, or blur keys",
                accentColor = Color(0xFF00E5FF),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchCameraMarkup()
                }
            )

            // 2. OCR Scan-to-StackTrace
            MagicToolCard(
                title = "🔍 Scan Terminal / Screen Error",
                subtitle = "Point camera at your computer screen or logs to auto-extract error",
                accentColor = Color(0xFFBC8CFF),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchOcrScan()
                }
            )

            // 3. Compare Design vs Reality
            MagicToolCard(
                title = "🔀 Compare Design vs Reality",
                subtitle = "Slide between Figma mockup and actual screenshot to spot pixel drift",
                accentColor = Color(0xFFFFB703),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchVisualCompare()
                }
            )

            Text(
                text = "VOICE & AUDIO",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8B949E),
                fontWeight = FontWeight.Bold
            )

            // 4. Voice Steps
            MagicToolCard(
                title = "🎙️ Speak Steps (Hands-Free)",
                subtitle = "Dictate reproduction steps while testing the device",
                accentColor = Color(0xFF38BDF8),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchVoiceSteps()
                }
            )

            // 5. Audio Glitch
            MagicToolCard(
                title = "🎵 Record Audio Glitch / Memo",
                subtitle = "Record speaker crackle or audio bugs with live waveform visualization",
                accentColor = Color(0xFFFF70A6),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchAudioGlitch()
                }
            )

            Text(
                text = "HARDWARE & SYSTEM LOGS",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8B949E),
                fontWeight = FontWeight.Bold
            )

            // 6. Device Vitals
            MagicToolCard(
                title = "⚡ Snapshot Device Specs & Battery",
                subtitle = "Log RAM, battery temp (°C), 120Hz display status, and storage",
                accentColor = Color(0xFF00FF88),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onSnapshotVitals()
                }
            )

            // 7. Logcat Dump
            MagicToolCard(
                title = "📋 Grab Crash Logs (Auto)",
                subtitle = "Instantly reads recent device fatal errors without tethering",
                accentColor = Color(0xFFFF5252),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onDumpLogcat()
                }
            )

            // 8. cURL Inspector
            MagicToolCard(
                title = "🌐 Network / cURL Inspector",
                subtitle = "Format JSON payloads and generate ready-to-run cURL commands",
                accentColor = Color(0xFF4CC9F0),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchCurlInspector()
                }
            )

            // 9. Bug Card Share
            MagicToolCard(
                title = "🎟️ Share OLED Bug Card",
                subtitle = "Export a stylized dark ticket card with QR code to Slack or Jira",
                accentColor = Color(0xFF00FF88),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchShareBugCard()
                }
            )

            // 10. Barcode Scanner
            MagicToolCard(
                title = "🏷️ Scan Test Device Asset Tag",
                subtitle = "Scan device QR code or barcode to link hardware IDs",
                accentColor = Color(0xFFE0AAFF),
                onClick = {
                    haptic.lightTap()
                    onDismiss()
                    onLaunchBarcodeScanner()
                }
            )
        }
    }
}

@Composable
private fun MagicToolCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color(0xFF161B22)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8B949E),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
