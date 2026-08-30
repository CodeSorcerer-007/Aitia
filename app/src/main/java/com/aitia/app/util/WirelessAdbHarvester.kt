package com.aitia.app.util

import android.content.Context
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class WirelessAdbTarget(
    val ipAddress: String,
    val port: Int,
    val pairingCode: String = "",
    val pairingPort: Int = 0
) {
    val connectCommand: String
        get() = "adb connect $ipAddress:$port"

    val pairCommand: String
        get() = if (pairingPort > 0 && pairingCode.isNotBlank()) "adb pair $ipAddress:$pairingPort $pairingCode" else ""

    val logcatHarvestCommand: String
        get() = "adb -s $ipAddress:$port logcat -d -t 250 -v time *:E"
}

object WirelessAdbHarvester {

    /**
     * Finds the local device Wi-Fi IPv4 address.
     */
    fun getLocalDeviceIpAddress(context: Context): String {
        return runCatching {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val wifiInfo = wifiManager?.connectionInfo
            val ipInt = wifiInfo?.ipAddress ?: 0
            if (ipInt != 0) {
                InetAddress.getByAddress(
                    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()
                ).hostAddress ?: "192.168.1.100"
            } else {
                "192.168.1.100"
            }
        }.getOrDefault("192.168.1.100")
    }

    /**
     * Builds standard step-by-step wireless ADB instructions.
     */
    fun getPairingInstructions(ipAddress: String, port: Int, code: String): String {
        return buildString {
            appendLine("### 📶 Wireless ADB Quick Guide (Android 11+)")
            appendLine("1. On target device, go to **Settings > Developer Options > Wireless Debugging**.")
            appendLine("2. Tap **Pair device with pairing code**.")
            if (code.isNotBlank()) {
                appendLine("3. Run on your host machine:")
                appendLine("   ```bash")
                appendLine("   adb pair $ipAddress:$port $code")
                appendLine("   ```")
            }
            appendLine("4. Connect:")
            appendLine("   ```bash")
            appendLine("   adb connect $ipAddress:$port")
            appendLine("   ```")
            appendLine("5. Capture remote logs:")
            appendLine("   ```bash")
            appendLine("   adb -s $ipAddress:$port logcat -d -t 200 *:E")
            appendLine("   ```")
        }
    }
}
