package com.aitia.app

import com.aitia.app.domain.parser.StackTraceParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StackTraceParserTest {

    @Test
    fun testParseStandardFatalException() {
        val stackTrace = """
            FATAL EXCEPTION: main
            Process: com.example.weather, PID: 18420
            java.lang.SecurityException: Permission Denial: starting Intent { act=android.media.action.IMAGE_CAPTURE }
                at android.app.Instrumentation.checkStartActivityResult(Instrumentation.java:2320)
                at android.app.Activity.startActivityForResult(Activity.java:5430)
                at com.example.weather.ui.profile.ProfileEditScreenKt.launchCamera(ProfileEditScreen.kt:84)
        """.trimIndent()

        val result = StackTraceParser.parse(stackTrace)

        assertTrue(result.isParsed)
        assertEquals("SecurityException", result.exceptionType)
        assertEquals("ProfileEditScreen.kt", result.sourceFile)
        assertEquals("84", result.sourceLine)
        assertTrue(result.errorMessage?.contains("Permission Denial") == true)
        assertEquals("SecurityException in ProfileEditScreen.kt:84", result.suggestedTitle)
    }

    @Test
    fun testParseNullPointerException() {
        val stackTrace = """
            java.lang.NullPointerException: Attempt to invoke virtual method on a null object reference
                at com.example.weather.HomeFragment.onViewCreated(HomeFragment.kt:54)
        """.trimIndent()

        val result = StackTraceParser.parse(stackTrace)

        assertTrue(result.isParsed)
        assertEquals("NullPointerException", result.exceptionType)
        assertEquals("HomeFragment.kt", result.sourceFile)
        assertEquals("54", result.sourceLine)
    }

    @Test
    fun testEmptyStackTraceReturnsUnparsed() {
        val result = StackTraceParser.parse("")
        assertTrue(!result.isParsed)
    }
}
