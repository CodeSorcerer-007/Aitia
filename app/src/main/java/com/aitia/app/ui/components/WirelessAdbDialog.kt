package com.aitia.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.DialogProperties
import com.aitia.app.util.WirelessAdbHarvester
import com.aitia.app.util.WirelessAdbTarget
import com.aitia.app.util.rememberHapticFeedback

@Composable
fun WirelessAdbDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()

    val localIp = remember { WirelessAdbHarvester.getLocalDeviceIpAddress(context) }
    var targetIp by remember { mutableStateOf(localIp) }
    var targetPort by remember { mutableStateOf("5555") }
    var pairingCode by remember { mutableStateOf("") }
    var pairingPort by remember { mutableStateOf("") }

    val target = remember(targetIp, targetPort, pairingCode, pairingPort) {
        WirelessAdbTarget(
            ipAddress = targetIp.ifBlank { "192.168.1.100" },
            port = targetPort.toIntOrNull() ?: 5555,
            pairingCode = pairingCode,
            pairingPort = pairingPort.toIntOrNull() ?: 0
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(20.dp)),
            color = Color(0xFF0D1117)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00FF88).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "📶 Wireless ADB Harvester",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Android 11+ Wi-Fi logcat & remote debug hub",
                                fontSize = 12.sp,
                                color = Color(0xFF8B949E)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8B949E))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Instructions
                    Surface(
                        color = Color(0xFF161B22),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("1. ENABLE ON TARGET DEVICE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Go to Settings > Developer Options > Wireless Debugging. Note the IP Address, Port, and Pairing Code.",
                                fontSize = 12.sp,
                                color = Color(0xFFC9D1D9)
                            )
                        }
                    }

                    // IP & Port Inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = targetIp,
                            onValueChange = { targetIp = it },
                            label = { Text("Target Device IP", fontSize = 11.sp) },
                            modifier = Modifier.weight(2f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00F0FF),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = targetPort,
                            onValueChange = { targetPort = it },
                            label = { Text("Port", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00F0FF),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }

                    // Optional Pairing inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = pairingCode,
                            onValueChange = { pairingCode = it },
                            label = { Text("Pairing Code (6 digits)", fontSize = 11.sp) },
                            modifier = Modifier.weight(2f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00FF88),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pairingPort,
                            onValueChange = { pairingPort = it },
                            label = { Text("Pair Port", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00FF88),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }

                    // Terminal Commands Box
                    Surface(
                        color = Color(0xFF161B22),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("READY-TO-RUN TERMINAL COMMANDS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00FF88))
                                TextButton(onClick = {
                                    haptic.success()
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val commands = buildString {
                                        if (target.pairCommand.isNotBlank()) appendLine(target.pairCommand)
                                        appendLine(target.connectCommand)
                                        appendLine(target.logcatHarvestCommand)
                                    }
                                    clipboard.setPrimaryClip(ClipData.newPlainText("ADB Commands", commands))
                                    Toast.makeText(context, "ADB commands copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy All", color = Color.White, fontSize = 11.sp)
                                }
                            }

                            if (target.pairCommand.isNotBlank()) {
                                Text(
                                    text = "# 1. Pair device:\n${target.pairCommand}\n",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFF7EE787)
                                )
                            }

                            Text(
                                text = "# 2. Connect device:\n${target.connectCommand}\n",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF7EE787)
                            )

                            Text(
                                text = "# 3. Harvest wireless logcat:\n${target.logcatHarvestCommand}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF00F0FF)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Logcat Harvest Command", target.logcatHarvestCommand))
                        Toast.makeText(context, "Logcat command copied!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Copy Wireless Logcat Harvester", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
