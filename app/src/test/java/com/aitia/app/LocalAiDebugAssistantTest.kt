package com.aitia.app

import com.aitia.app.domain.insights.LocalAiDebugAssistant
import com.aitia.app.domain.model.Issue
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAiDebugAssistantTest {

    @Test
    fun testAnalyzeIssueGeneratesDiagnosisAndCodeSnippet() {
        val issue = Issue(
            id = 42,
            title = "App crashes on profile edit",
            description = "Attempting to change photo crashes immediately",
            type = IssueType.CRASH,
            priority = Priority.CRITICAL,
            status = IssueStatus.OPEN,
            exceptionType = "NullPointerException",
            errorMessage = "Attempt to invoke virtual method 'String.length()' on a null object reference",
            sourceFile = "ProfileViewModel.kt",
            sourceLine = "128"
        )

        val analysis = LocalAiDebugAssistant.analyzeIssue(issue)

        assertNotNull(analysis)
        assertTrue(analysis.text.contains("AI Defect Diagnosis"))
        assertTrue(analysis.text.contains("Plain English Explanation"))
        assertTrue(analysis.text.contains("ProfileViewModel.kt"))
        assertNotNull(analysis.codeSnippet)
    }

    @Test
    fun testGenerateUnitTestProducesExecutableJUnitTest() {
        val issue = Issue(
            id = 88,
            title = "IndexOutOfBoundsException when list is empty",
            type = IssueType.CRASH,
            priority = Priority.HIGH,
            status = IssueStatus.OPEN,
            exceptionType = "IndexOutOfBoundsException",
            sourceFile = "ItemListAdapter.kt"
        )

        val unitTestMsg = LocalAiDebugAssistant.generateUnitTest(issue)

        assertNotNull(unitTestMsg)
        assertNotNull(unitTestMsg.codeSnippet)
        assertTrue(unitTestMsg.codeSnippet!!.contains("class ItemListAdapterTest"))
        assertTrue(unitTestMsg.codeSnippet!!.contains("@Test"))
        assertTrue(unitTestMsg.codeSnippet!!.contains("testEmptyListBoundaryCondition"))
    }

    @Test
    fun testAnswerUserQueryHandlesConversationalRequests() {
        val issue = Issue(
            id = 10,
            title = "Database constraint failure",
            type = IssueType.BUG,
            priority = Priority.HIGH,
            status = IssueStatus.OPEN,
            exceptionType = "SQLiteConstraintException"
        )

        val testQueryResponse = LocalAiDebugAssistant.answerUserQuery("Can you write a unit test for this?", issue)
        assertTrue(testQueryResponse.codeSnippet?.contains("@Test") == true)

        val eli5QueryResponse = LocalAiDebugAssistant.answerUserQuery("Explain this in plain English (ELI5)", issue)
        assertTrue(eli5QueryResponse.text.contains("Plain English"))

        val codeFixResponse = LocalAiDebugAssistant.answerUserQuery("How to fix this code?", issue)
        assertNotNull(codeFixResponse.codeSnippet)
    }
}
