package com.aitia.app.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.provider.Settings
import java.util.Locale

data class DeviceVitalsSnapshot(
    val batteryPercentage: Int,
    val batteryTemperatureC: Float,
    val isCharging: Boolean,
    val isPowerSaveMode: Boolean,
    val thermalStatus: String,
    val availableRamMb: Long,
    val totalRamMb: Long,
    val isLowMemory: Boolean,
    val availableStorageGb: Float,
    val totalStorageGb: Float,
    val networkType: String,
    val isVpnActive: Boolean,
    val isAirplaneMode: Boolean,
    val screenResolution: String,
    val screenDensityDpi: Int,
    val refreshRateHz: Float,
    val isDarkMode: Boolean,
    val fontScale: Float,
    val osVersion: String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
    val deviceModel: String = "${Build.MANUFACTURER} ${Build.MODEL}"
) {
    val ramUsedPercentage: Int
        get() = if (totalRamMb > 0) (((totalRamMb - availableRamMb).toDouble() / totalRamMb) * 100).toInt() else 0

    val storageUsedPercentage: Int
        get() = if (totalStorageGb > 0) (((totalStorageGb - availableStorageGb) / totalStorageGb) * 100).toInt() else 0
}

object DeviceVitalsHarvester {

    fun capture(context: Context): DeviceVitalsSnapshot {
        // 1. Battery & Power
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 0

        val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val batteryTempC = tempTenths / 10.0f

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isPowerSave = powerManager?.isPowerSaveMode ?: false

        val thermalStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
            when (powerManager.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> "Nominal"
                PowerManager.THERMAL_STATUS_LIGHT -> "Light Throttling"
                PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Throttling"
                PowerManager.THERMAL_STATUS_SEVERE -> "Severe Throttling"
                PowerManager.THERMAL_STATUS_CRITICAL -> "Critical Throttling"
                PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency Throttling"
                PowerManager.THERMAL_STATUS_SHUTDOWN -> "Shutdown"
                else -> "Normal"
            }
        } else {
            "Normal"
        }

        // 2. Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val availRamMb = memInfo.availMem / (1024 * 1024)
        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val isLowMem = memInfo.lowMemory

        // 3. Storage
        val dataPath = Environment.getDataDirectory().path
        val statFs = StatFs(dataPath)
        val blockSize = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        val availBlocks = statFs.availableBlocksLong
        val totalStorageGb = (totalBlocks * blockSize) / (1024f * 1024f * 1024f)
        val availStorageGb = (availBlocks * blockSize) / (1024f * 1024f * 1024f)

        // 4. Connectivity
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connManager?.activeNetwork
        val caps = connManager?.getNetworkCapabilities(activeNetwork)

        val networkType = when {
            caps == null -> "Offline"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular (5G/LTE)"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth Tethering"
            else -> "Connected"
        }
        val isVpnActive = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
        val isAirplane = Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0

        // 5. Display
        val dm = context.resources.displayMetrics
        val screenRes = "${dm.widthPixels} x ${dm.heightPixels} px (${(dm.widthPixels / dm.density).toInt()} x ${(dm.heightPixels / dm.density).toInt()} dp)"
        val densityDpi = dm.densityDpi
        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val fontScale = context.resources.configuration.fontScale

        // Display refresh rate
        val refreshRate = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.refreshRate ?: 60.0f
            } else {
                @Suppress("DEPRECATION")
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as? android.view.WindowManager
                wm?.defaultDisplay?.refreshRate ?: 60.0f
            }
        }.getOrDefault(60.0f)

        return DeviceVitalsSnapshot(
            batteryPercentage = batteryPct,
            batteryTemperatureC = batteryTempC,
            isCharging = isCharging,
            isPowerSaveMode = isPowerSave,
            thermalStatus = thermalStatus,
            availableRamMb = availRamMb,
            totalRamMb = totalRamMb,
            isLowMemory = isLowMem,
            availableStorageGb = availStorageGb,
            totalStorageGb = totalStorageGb,
            networkType = networkType,
            isVpnActive = isVpnActive,
            isAirplaneMode = isAirplane,
            screenResolution = screenRes,
            screenDensityDpi = densityDpi,
            refreshRateHz = refreshRate,
            isDarkMode = isDarkMode,
            fontScale = fontScale
        )
    }

    fun formatMarkdown(v: DeviceVitalsSnapshot): String {
        return buildString {
            appendLine("### ⚡ Device & System Vitals Telemetry")
            appendLine("- **Device:** ${v.deviceModel} · ${v.osVersion}")
            appendLine("- **Power:** ${v.batteryPercentage}% (${if (v.isCharging) "Charging" else "Discharging"}) · Temp: ${String.format(Locale.US, "%.1f", v.batteryTemperatureC)}°C · Thermal: ${v.thermalStatus}")
            appendLine("- **Memory (RAM):** ${v.availableRamMb} MB free / ${v.totalRamMb} MB total (${v.ramUsedPercentage}% used) · LowMem: ${v.isLowMemory}")
            appendLine("- **Storage:** ${String.format(Locale.US, "%.1f", v.availableStorageGb)} GB free / ${String.format(Locale.US, "%.1f", v.totalStorageGb)} GB total (${v.storageUsedPercentage}% used)")
            appendLine("- **Network:** ${v.networkType} · VPN: ${if (v.isVpnActive) "Active" else "None"} · Airplane: ${v.isAirplaneMode}")
            appendLine("- **Display:** ${v.screenResolution} · ${v.screenDensityDpi} dpi · ${String.format(Locale.US, "%.0f", v.refreshRateHz)} Hz · DarkMode: ${v.isDarkMode} · FontScale: ${v.fontScale}x")
        }
    }
}
