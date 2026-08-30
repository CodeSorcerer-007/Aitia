package com.aitia.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.aitia.app.util.AudioRecorderHelper
import java.io.File

@Composable
fun AudioGlitchRecorderDialog(
    onDismiss: () -> Unit,
    onAudioRecorded: (File) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audioHelper = remember { AudioRecorderHelper(context) }

    val isRecording by audioHelper.isRecording.collectAsStateWithLifecycle()
    val isPlaying by audioHelper.isPlaying.collectAsStateWithLifecycle()
    val currentAmp by audioHelper.currentAmplitude.collectAsStateWithLifecycle()
    val amplitudes by audioHelper.amplitudes.collectAsStateWithLifecycle()
    val durationSeconds by audioHelper.durationSeconds.collectAsStateWithLifecycle()

    var recordedFile by remember { mutableStateOf<File?>(null) }
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasRecordPermission = granted
    }

    DisposableEffect(Unit) {
        onDispose {
            audioHelper.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp)),
            color = Color(0xFF0D1117),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hardware & Audio Glitch Note",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Waveform Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF161B22))
                        .border(1.dp, Color(0xFF21262D), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val barWidth = 8f
                        val gap = 6f
                        val maxBars = (canvasWidth / (barWidth + gap)).toInt()

                        val displayAmps = amplitudes.takeLast(maxBars)
                        val startX = canvasWidth - (displayAmps.size * (barWidth + gap))

                        displayAmps.forEachIndexed { index, amp ->
                            val barHeight = (canvasHeight * amp).coerceAtLeast(6f)
                            val x = startX + index * (barWidth + gap)
                            val y = (canvasHeight - barHeight) / 2f

                            drawRoundRect(
                                color = if (isRecording) Color(0xFFFF5252) else Color(0xFF00FF88),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }

                    if (!isRecording && recordedFile == null) {
                        Text(
                            text = "Tap Record to capture audio artifact / glitch",
                            color = Color(0xFF8B949E),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer
                val minutes = durationSeconds / 60
                val seconds = durationSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    color = if (isRecording) Color(0xFFFF5252) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Controls
                if (!hasRecordPermission) {
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Grant Microphone Permission", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF5252))
                            .clickable {
                                recordedFile = audioHelper.stopRecording()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                } else if (recordedFile != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play / Pause Button
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    audioHelper.stopPlayback()
                                } else {
                                    audioHelper.play(recordedFile!!)
                                }
                            },
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF21262D))
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Stop" else "Play",
                                tint = Color(0xFF00FF88)
                            )
                        }

                        // Re-record
                        Button(
                            onClick = {
                                recordedFile = null
                                audioHelper.startRecording(scope)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Re-record", color = Color.White)
                        }

                        // Save Button
                        Button(
                            onClick = {
                                onAudioRecorded(recordedFile!!)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attach", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Record Button
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00FF88))
                            .clickable {
                                recordedFile = null
                                audioHelper.startRecording(scope)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Record", tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}
