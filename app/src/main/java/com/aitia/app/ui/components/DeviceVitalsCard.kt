package com.aitia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.aitia.app.util.DeviceVitalsHarvester
import com.aitia.app.util.DeviceVitalsSnapshot
import java.util.Locale

@Composable
fun DeviceVitalsCard(
    modifier: Modifier = Modifier,
    initialSnapshot: DeviceVitalsSnapshot? = null,
    onSnapshotCaptured: ((DeviceVitalsSnapshot) -> Unit)? = null
) {
    val context = LocalContext.current
    var vitals by remember {
        mutableStateOf(initialSnapshot ?: DeviceVitalsHarvester.capture(context))
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0D1117),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF21262D))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (vitals.isLowMemory) Color(0xFFFF5252) else Color(0xFF00FF88))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Device & Hardware Telemetry",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = {
                        val fresh = DeviceVitalsHarvester.capture(context)
                        vitals = fresh
                        onSnapshotCaptured?.invoke(fresh)
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF58A6FF), modifier = Modifier.size(16.dp))
                }
            }

            // Quick Info Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Power & Thermal Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF161B22)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color(0xFF00FF88), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Battery", fontSize = 11.sp, color = Color(0xFF8B949E))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${vitals.batteryPercentage}% · ${String.format(Locale.US, "%.1f", vitals.batteryTemperatureC)}°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(vitals.thermalStatus, fontSize = 10.sp, color = Color(0xFF00F0FF))
                    }
                }

                // Network & Display Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF161B22)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = Color(0xFF58A6FF), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Network", fontSize = 11.sp, color = Color(0xFF8B949E))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(vitals.networkType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${String.format(Locale.US, "%.0f", vitals.refreshRateHz)} Hz · ${if (vitals.isVpnActive) "VPN" else "No VPN"}", fontSize = 10.sp, color = Color(0xFFBC8CFF))
                    }
                }
            }

            // RAM Bar
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = Color(0xFFFFA726), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RAM Usage", fontSize = 10.sp, color = Color(0xFF8B949E))
                    }
                    Text("${vitals.availableRamMb} MB free / ${vitals.totalRamMb} MB (${vitals.ramUsedPercentage}%)", fontSize = 10.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                }
                LinearProgressIndicator(
                    progress = { vitals.ramUsedPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (vitals.isLowMemory) Color(0xFFFF5252) else Color(0xFFFFA726),
                    trackColor = Color(0xFF21262D)
                )
            }

            // Storage Bar
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Storage", fontSize = 10.sp, color = Color(0xFF8B949E))
                    }
                    Text("${String.format(Locale.US, "%.1f", vitals.availableStorageGb)} GB free / ${String.format(Locale.US, "%.1f", vitals.totalStorageGb)} GB", fontSize = 10.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                }
                LinearProgressIndicator(
                    progress = { vitals.storageUsedPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF00F0FF),
                    trackColor = Color(0xFF21262D)
                )
            }
        }
    }
}
