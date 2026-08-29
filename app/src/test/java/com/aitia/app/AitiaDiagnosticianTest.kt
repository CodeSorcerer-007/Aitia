package com.aitia.app

import com.aitia.app.domain.insights.AitiaDiagnostician
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AitiaDiagnosticianTest {

    @Test
    fun testDiagnoseNullPointerException() {
        val diagnosis = AitiaDiagnostician.diagnose(
            exceptionType = "NullPointerException",
            errorMessage = "Attempt to invoke virtual method on a null object reference"
        )

        assertNotNull(diagnosis)
        diagnosis!!
        assertEquals("Null Pointer Dereference", diagnosis.title)
        assertTrue(diagnosis.recommendedFixCode.contains("?."))
        assertTrue(diagnosis.commonPitfalls.isNotEmpty())
    }

    @Test
    fun testDiagnoseUninitializedPropertyAccessException() {
        val diagnosis = AitiaDiagnostician.diagnose(
            exceptionType = "UninitializedPropertyAccessException",
            errorMessage = "lateinit property myRepository has not been initialized"
        )

        assertNotNull(diagnosis)
        diagnosis!!
        assertEquals("Lateinit Property Not Initialized", diagnosis.title)
        assertTrue(diagnosis.recommendedFixCode.contains("isInitialized"))
    }

    @Test
    fun testDiagnoseSecurityPermissionDenial() {
        val diagnosis = AitiaDiagnostician.diagnose(
            exceptionType = "SecurityException",
            errorMessage = "Permission Denial: starting Intent requires android.permission.CAMERA"
        )

        assertNotNull(diagnosis)
        diagnosis!!
        assertEquals("Security Exception / Missing Permission", diagnosis.title)
        assertTrue(diagnosis.recommendedFixCode.contains("checkSelfPermission"))
    }

    @Test
    fun testDiagnoseDatabaseForeignKeyConstraint() {
        val diagnosis = AitiaDiagnostician.diagnose(
            exceptionType = "SQLiteConstraintException",
            errorMessage = "FOREIGN KEY constraint failed (code 787)"
        )

        assertNotNull(diagnosis)
        diagnosis!!
        assertEquals("Room Database Constraint Violation", diagnosis.title)
        assertTrue(diagnosis.recommendedFixCode.contains("OnConflictStrategy"))
    }

    @Test
    fun testDiagnoseGeneralUnknownReturnsNull() {
        val diagnosis = AitiaDiagnostician.diagnose(
            exceptionType = "CustomBusinessLogicError",
            errorMessage = "User cannot purchase item due to balance"
        )

        assertEquals(null, diagnosis)
    }
}
