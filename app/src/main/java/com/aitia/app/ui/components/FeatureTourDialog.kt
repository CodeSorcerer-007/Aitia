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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple
import com.aitia.app.util.rememberHapticFeedback
import kotlinx.coroutines.launch

private data class TourStep(
    val title: String,
    val icon: ImageVector,
    val headline: String,
    val explanation: String,
    val proTip: String,
    val accentColor: Color
)

@Composable
fun FeatureTourDialog(
    onDismiss: () -> Unit,
    onTryFeature: ((String) -> Unit)? = null
) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    val scope = rememberCoroutineScope()
    val haptic = rememberHapticFeedback()

    val tourSteps = listOf(
        TourStep(
            title = "1. Snap & Draw",
            icon = Icons.Default.CameraAlt,
            headline = "Circle the problem in 2 seconds",
            explanation = "Take a photo of any bug or screen glitch. Draw arrows, boxes, or use the Blur brush to hide private passwords or API keys.",
            proTip = "💡 Pro Tip: Tap '📸 Snap & Markup' from any ticket or quick capture!",
            accentColor = Color(0xFF00E5FF)
        ),
        TourStep(
            title = "2. Scan Terminal (OCR)",
            icon = Icons.Default.DocumentScanner,
            headline = "Point your camera at your laptop monitor",
            explanation = "No need to copy-paste long stack traces! Point your phone camera at terminal errors or Android Studio logs to auto-extract error types and file lines.",
            proTip = "💡 Pro Tip: 100% on-device ML Kit text recognition — no internet required.",
            accentColor = Color(0xFFBC8CFF)
        ),
        TourStep(
            title = "3. Shake to Report",
            icon = Icons.Default.Vibration,
            headline = "Found a bug while testing? Just shake your phone!",
            explanation = "Whenever you're testing on a physical device, giving it a quick shake pops up the Quick Capture sheet immediately.",
            proTip = "💡 Pro Tip: Enable or disable anytime in Settings → QA Sensors.",
            accentColor = Color(0xFFFFB703)
        ),
        TourStep(
            title = "4. Speak Your Steps",
            icon = Icons.Default.Mic,
            headline = "Hands-free voice reproduction steps",
            explanation = "Say what you did out loud ('I clicked login, then tapped settings, then it crashed'). The app automatically creates numbered steps (1., 2., 3.).",
            proTip = "💡 Pro Tip: Tap '🎙️ Dictate' inside the issue steps section.",
            accentColor = Color(0xFF38BDF8)
        ),
        TourStep(
            title = "5. Smart Fix Advisor",
            icon = Icons.Default.Lightbulb,
            headline = "Instant root cause explanations (ELI5)",
            explanation = "Whenever an exception is detected, Αἰτία explains why it broke in plain English and gives you the exact Kotlin fix code to copy-paste.",
            proTip = "💡 Pro Tip: Toggle 'In Plain English' vs 'Developer Mode' anytime!",
            accentColor = Color(0xFF00FF88)
        ),
        TourStep(
            title = "6. Shareable Bug Cards",
            icon = Icons.Default.QrCode2,
            headline = "Drop gorgeous OLED cards into Slack or Jira",
            explanation = "Export stylized dark-mode ticket cards with glowing priority tags and QR codes so teammates can reproduce defects instantly.",
            proTip = "💡 Pro Tip: Tap the Share icon on top of any issue to export.",
            accentColor = Color(0xFFFF70A6)
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color(0xFF0D1117),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "How Aitia Works",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF8B949E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                ) { page ->
                    val step = tourSteps[page]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(step.accentColor.copy(alpha = 0.15f))
                                .border(1.5.dp, step.accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = step.accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = step.accentColor
                        )

                        Text(
                            text = step.headline,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = step.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFC9D1D9),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF161B22),
                            border = androidx.compose.foundation.BorderStroke(1.dp, step.accentColor.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = step.proTip,
                                style = MaterialTheme.typography.bodySmall,
                                color = step.accentColor,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(6) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 16.dp else 6.dp, 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) Color(0xFF00E5FF)
                                    else Color(0xFF30363D)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                haptic.lightTap()
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back", color = Color(0xFFC9D1D9))
                        }
                    }

                    Button(
                        onClick = {
                            haptic.lightTap()
                            if (pagerState.currentPage < 5) {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                    ) {
                        Text(
                            text = if (pagerState.currentPage == 5) "Got it! 🚀" else "Next →",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
