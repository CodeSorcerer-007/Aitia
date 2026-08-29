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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class DeviceTestStatus(val label: String, val color: Color) {
    PENDING("Pending", Color(0xFF8B949E)),
    PASS("Passed", Color(0xFF00FF88)),
    FAIL("Failed", Color(0xFFFF5252))
}

data class DeviceMatrixEntry(
    val deviceName: String,
    val osVersion: String,
    val status: DeviceTestStatus = DeviceTestStatus.PENDING,
    val notes: String = ""
)

@Composable
fun CrossDeviceVerificationMatrix(
    modifier: Modifier = Modifier,
    initialEntries: List<DeviceMatrixEntry> = listOf(
        DeviceMatrixEntry("OnePlus Nord 5", "Android 16 (API 36)", DeviceTestStatus.PASS, "No crash on resume"),
        DeviceMatrixEntry("Pixel 8", "Android 15 (API 35)", DeviceTestStatus.PASS, "Verified layout"),
        DeviceMatrixEntry("Galaxy Fold 5", "Android 14 (Foldable)", DeviceTestStatus.PENDING, "Pending hinge fold test")
    ),
    onMatrixUpdated: ((List<DeviceMatrixEntry>) -> Unit)? = null
) {
    val entries = remember { mutableStateListOf<DeviceMatrixEntry>().apply { addAll(initialEntries) } }
    var isAddingDevice by remember { mutableStateOf(false) }
    var newDeviceName by remember { mutableStateOf("") }
    var newDeviceOs by remember { mutableStateOf("Android 16") }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0D1117),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF21262D))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cross-Device Verification Matrix", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                IconButton(
                    onClick = { isAddingDevice = !isAddingDevice },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Device", tint = Color(0xFF00FF88), modifier = Modifier.size(18.dp))
                }
            }

            // Entries List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.forEachIndexed { index, entry ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF161B22),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (entry.status) {
                                DeviceTestStatus.PASS -> Color(0xFF00FF88).copy(alpha = 0.3f)
                                DeviceTestStatus.FAIL -> Color(0xFFFF5252).copy(alpha = 0.3f)
                                else -> Color(0xFF21262D)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.deviceName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${entry.osVersion}${if (entry.notes.isNotBlank()) " · ${entry.notes}" else ""}", color = Color(0xFF8B949E), fontSize = 11.sp)
                            }

                            // Cycle Status Button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(entry.status.color.copy(alpha = 0.15f))
                                    .clickable {
                                        val nextStatus = when (entry.status) {
                                            DeviceTestStatus.PENDING -> DeviceTestStatus.PASS
                                            DeviceTestStatus.PASS -> DeviceTestStatus.FAIL
                                            DeviceTestStatus.FAIL -> DeviceTestStatus.PENDING
                                        }
                                        entries[index] = entry.copy(status = nextStatus)
                                        onMatrixUpdated?.invoke(entries.toList())
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (entry.status) {
                                        DeviceTestStatus.PASS -> Icons.Default.Check
                                        DeviceTestStatus.FAIL -> Icons.Default.Close
                                        else -> Icons.Default.HourglassEmpty
                                    },
                                    contentDescription = null,
                                    tint = entry.status.color,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(entry.status.label, color = entry.status.color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Inline Add Device Row
            if (isAddingDevice) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B22), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newDeviceName,
                        onValueChange = { newDeviceName = it },
                        placeholder = { Text("Device (e.g. Pixel Tablet)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00FF88),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )

                    Button(
                        onClick = {
                            if (newDeviceName.isNotBlank()) {
                                entries.add(DeviceMatrixEntry(newDeviceName, newDeviceOs, DeviceTestStatus.PENDING))
                                newDeviceName = ""
                                isAddingDevice = false
                                onMatrixUpdated?.invoke(entries.toList())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add to Matrix", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
