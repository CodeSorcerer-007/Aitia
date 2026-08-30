package com.aitia.app

import com.aitia.app.util.WirelessAdbHarvester
import com.aitia.app.util.WirelessAdbTarget
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WirelessAdbHarvesterTest {

    @Test
    fun testWirelessAdbTargetGeneratesValidCommands() {
        val target = WirelessAdbTarget(
            ipAddress = "192.168.1.150",
            port = 5555,
            pairingCode = "123456",
            pairingPort = 37123
        )

        assertNotNull(target.connectCommand)
        assertTrue(target.connectCommand == "adb connect 192.168.1.150:5555")
        assertTrue(target.pairCommand == "adb pair 192.168.1.150:37123 123456")
        assertTrue(target.logcatHarvestCommand.contains("adb -s 192.168.1.150:5555 logcat"))
    }

    @Test
    fun testGetPairingInstructionsIncludesNumberedSteps() {
        val guide = WirelessAdbHarvester.getPairingInstructions("192.168.1.50", 5555, "654321")

        assertNotNull(guide)
        assertTrue(guide.contains("Wireless ADB Quick Guide"))
        assertTrue(guide.contains("adb pair 192.168.1.50"))
        assertTrue(guide.contains("adb connect 192.168.1.50:5555"))
    }
}
