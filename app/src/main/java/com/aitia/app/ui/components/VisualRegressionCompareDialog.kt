package com.aitia.app.ui.components

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File

@Composable
fun VisualRegressionCompareDialog(
    actualImageFile: File,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val actualBitmap = remember(actualImageFile) {
        BitmapFactory.decodeFile(actualImageFile.absolutePath)
    }

    var expectedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var comparisonMode by remember { mutableIntStateOf(0) } // 0: Split Slider, 1: Opacity Overlay
    var splitPosition by remember { mutableFloatStateOf(0.5f) }
    var opacityLevel by remember { mutableFloatStateOf(0.5f) }

    val expectedPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    expectedBitmap = BitmapFactory.decodeStream(stream)
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B22))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    Text(
                        text = "Visual Regression Comparer",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Button(
                        onClick = { expectedPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58A6FF)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (expectedBitmap == null) "Load Mockup" else "Change", fontSize = 12.sp)
                    }
                }

                // Mode Tabs
                TabRow(
                    selectedTabIndex = comparisonMode,
                    containerColor = Color(0xFF0D1117),
                    contentColor = Color(0xFF00F0FF),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[comparisonMode]),
                            color = Color(0xFF00F0FF)
                        )
                    }
                ) {
                    Tab(
                        selected = comparisonMode == 0,
                        onClick = { comparisonMode = 0 },
                        text = { Text("Split Slider", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = comparisonMode == 1,
                        onClick = { comparisonMode = 1 },
                        text = { Text("Alpha Overlay Blend", fontWeight = FontWeight.Bold) }
                    )
                }

                // Canvas Comparator Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF080A0E))
                ) {
                    if (actualBitmap != null && expectedBitmap != null) {
                        val actualImg = actualBitmap.asImageBitmap()
                        val expectedImg = expectedBitmap!!.asImageBitmap()

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(comparisonMode) {
                                    if (comparisonMode == 0) {
                                        detectDragGestures { change, _ ->
                                            change.consume()
                                            splitPosition = (change.position.x / size.width).coerceIn(0.05f, 0.95f)
                                        }
                                    }
                                }
                        ) {
                            val w = size.width
                            val h = size.height

                            if (comparisonMode == 0) {
                                // Split Slider Mode
                                val splitX = w * splitPosition

                                // Draw Expected (Left portion)
                                clipRect(left = 0f, top = 0f, right = splitX, bottom = h) {
                                    drawImage(expectedImg, dstSize = androidx.compose.ui.unit.IntSize(w.toInt(), h.toInt()))
                                }

                                // Draw Actual (Right portion)
                                clipRect(left = splitX, top = 0f, right = w, bottom = h) {
                                    drawImage(actualImg, dstSize = androidx.compose.ui.unit.IntSize(w.toInt(), h.toInt()))
                                }

                                // Divider Line & Handle
                                drawLine(
                                    color = Color(0xFF00F0FF),
                                    start = Offset(splitX, 0f),
                                    end = Offset(splitX, h),
                                    strokeWidth = 4f
                                )
                                drawCircle(
                                    color = Color(0xFF00F0FF),
                                    radius = 24f,
                                    center = Offset(splitX, h / 2)
                                )
                            } else {
                                // Alpha Overlay Mode
                                drawImage(
                                    image = expectedImg,
                                    dstSize = androidx.compose.ui.unit.IntSize(w.toInt(), h.toInt()),
                                    alpha = 1f - opacityLevel
                                )
                                drawImage(
                                    image = actualImg,
                                    dstSize = androidx.compose.ui.unit.IntSize(w.toInt(), h.toInt()),
                                    alpha = opacityLevel
                                )
                            }
                        }

                        // Labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(6.dp)) {
                                Text("DESIGN MOCKUP (Expected)", color = Color(0xFF58A6FF), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(6.dp)) {
                                Text("DEFECT SNAPSHOT (Actual)", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    } else {
                        // Empty State: Prompt to select design mockup
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Compare, contentDescription = null, tint = Color(0xFF58A6FF), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Load Design Mockup / Figma Spec", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Select a reference screenshot to compare pixel-for-pixel against your captured bug.",
                                color = Color(0xFF8B949E),
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { expectedPickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Select Mockup Image", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Bottom Control Slider
                if (actualBitmap != null && expectedBitmap != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF161B22))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        if (comparisonMode == 0) {
                            Text(
                                text = "Drag slider or swipe canvas to inspect visual difference",
                                color = Color(0xFF8B949E),
                                fontSize = 12.sp
                            )
                            Slider(
                                value = splitPosition,
                                onValueChange = { splitPosition = it },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF00F0FF),
                                    activeTrackColor = Color(0xFF00F0FF),
                                    inactiveTrackColor = Color(0xFF30363D)
                                )
                            )
                        } else {
                            Text(
                                text = "Blend: ${((1f - opacityLevel) * 100).toInt()}% Design · ${(opacityLevel * 100).toInt()}% Bug",
                                color = Color(0xFF8B949E),
                                fontSize = 12.sp
                            )
                            Slider(
                                value = opacityLevel,
                                onValueChange = { opacityLevel = it },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF00FF88),
                                    activeTrackColor = Color(0xFF00FF88),
                                    inactiveTrackColor = Color(0xFF30363D)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
