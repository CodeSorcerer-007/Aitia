package com.aitia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.CodeBackground
import com.aitia.app.ui.theme.MonospaceCode
import com.aitia.app.util.rememberHapticFeedback

@Composable
fun CodeViewer(
    codeText: String,
    onCodeChange: ((String) -> Unit)? = null,
    onParseStackTrace: (() -> Unit)? = null,
    title: String = "Technical Logs & Stack Trace",
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val haptic = rememberHapticFeedback()
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        color = CodeBackground
    ) {
        Column {
            // Action bar header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onParseStackTrace != null && codeText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                haptic.lightTap()
                                onParseStackTrace()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = "Parse Stack Trace",
                                tint = AitiaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (onCodeChange != null) {
                        IconButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    haptic.lightTap()
                                    onCodeChange(clip)
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste Logs",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (codeText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(codeText))
                                haptic.success()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        if (onCodeChange != null) {
                            IconButton(
                                onClick = {
                                    haptic.lightTap()
                                    onCodeChange("")
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Code Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(12.dp)
            ) {
                if (codeText.isBlank()) {
                    Text(
                        text = "No logs or stack trace attached.\nPaste error logs to auto-extract Exception and Source File.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = MonospaceCode,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                } else {
                    Text(
                        text = codeText,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = MonospaceCode,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}
