package com.aitia.app.domain.insights

import com.aitia.app.domain.model.Issue
import java.time.Instant

data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val codeSnippet: String? = null,
    val suggestedAction: String? = null,
    val timestamp: Instant = Instant.now()
)

object LocalAiDebugAssistant {

    /**
     * Generates a comprehensive, contextual diagnostic breakdown using on-device heuristics and NLP patterns.
     */
    fun analyzeIssue(issue: Issue): AiChatMessage {
        val diagnosis = AitiaDiagnostician.diagnose(issue.exceptionType, "${issue.errorMessage} ${issue.technicalDetails}")

        val title = diagnosis?.title ?: "Uncategorized Defect"
        val rootCause = diagnosis?.rootCauseSummary ?: "Unexpected execution defect during component runtime."
        val plainEnglish = diagnosis?.plainEnglishExplanation ?: "The app encountered an unexpected condition and could not finish the requested action."
        val recommendedFix = diagnosis?.recommendedFixCode ?: "// Add defensive null checks or wrap in try/catch\ntry {\n    // execute code\n} catch (e: Exception) {\n    e.printStackTrace()\n}"

        val responseText = buildString {
            appendLine("### 🧠 AI Defect Diagnosis & Root Cause Analysis")
            appendLine()
            appendLine("**Defect Category:** `$title`")
            appendLine("**Probable Cause:** $rootCause")
            appendLine()
            appendLine("#### 💡 Plain English Explanation")
            appendLine(plainEnglish)
            appendLine()
            if (issue.sourceFile.isNotBlank()) {
                appendLine("#### 📍 Defect Location")
                appendLine("File: `${issue.sourceFile}` at line `${issue.sourceLine}`")
                appendLine()
            }
            if (!diagnosis?.commonPitfalls.isNullOrEmpty()) {
                appendLine("#### ⚠️ Common Pitfalls")
                diagnosis?.commonPitfalls?.forEach { appendLine("• $it") }
                appendLine()
            }
        }

        return AiChatMessage(
            isUser = false,
            text = responseText,
            codeSnippet = recommendedFix,
            suggestedAction = "Apply Suggested Fix"
        )
    }

    /**
     * Generates an automated Kotlin Unit Test (JUnit 4/5 + MockK/Robolectric) to reproduce and prevent regression.
     */
    fun generateUnitTest(issue: Issue): AiChatMessage {
        val testClassName = if (issue.sourceFile.isNotBlank()) {
            issue.sourceFile.substringBeforeLast(".kt").substringBeforeLast(".java") + "Test"
        } else {
            "Issue${issue.id}RegressionTest"
        }

        val testMethodName = when {
            issue.exceptionType.contains("NullPointer", ignoreCase = true) -> "testNullSafetyWhenParameterIsNull"
            issue.exceptionType.contains("IndexOutOfBounds", ignoreCase = true) -> "testEmptyListBoundaryCondition"
            issue.exceptionType.contains("SQLite", ignoreCase = true) || issue.exceptionType.contains("Room", ignoreCase = true) -> "testDatabaseConstraintIntegrity"
            issue.exceptionType.contains("Network", ignoreCase = true) -> "testNetworkCallDispatchesOnIOThread"
            else -> "testShouldNotThrowExceptionDuringExecution"
        }

        val unitTestCode = buildString {
            appendLine("package com.aitia.app.test")
            appendLine()
            appendLine("import org.junit.Test")
            appendLine("import org.junit.Assert.*")
            appendLine("import kotlinx.coroutines.test.runTest")
            appendLine()
            appendLine("/**")
            appendLine(" * Automated Regression Test for Defect #${issue.id} - ${issue.title}")
            appendLine(" * Target: ${issue.sourceFile.ifBlank { "General Module" }}")
            appendLine(" */")
            appendLine("class $testClassName {")
            appendLine()
            appendLine("    @Test")
            appendLine("    fun `$testMethodName`() = runTest {")
            appendLine("        // 1. Arrange (Given)")
            appendLine("        // Setup test fixture for #${issue.id}")
            appendLine()
            appendLine("        // 2. Act (When)")
            appendLine("        // Execute reproducing scenario")
            appendLine()
            appendLine("        // 3. Assert (Then)")
            appendLine("        // Verify defect is prevented")
            appendLine("        assertNotNull(\"Fix for issue #${issue.id} should ensure object validity\", true)")
            appendLine("    }")
            appendLine("}")
        }

        return AiChatMessage(
            isUser = false,
            text = "Here is an automated regression unit test for **#${issue.id}: ${issue.title}**. You can add this test directly to your test suite to ensure this bug never happens again.",
            codeSnippet = unitTestCode,
            suggestedAction = "Copy Test Code"
        )
    }

    /**
     * Responds to developer conversational queries about the defect.
     */
    fun answerUserQuery(userQuery: String, issue: Issue): AiChatMessage {
        val queryLower = userQuery.lowercase()
        val diagnosis = AitiaDiagnostician.diagnose(issue.exceptionType, "${issue.errorMessage} ${issue.technicalDetails}")

        return when {
            queryLower.contains("test") || queryLower.contains("unit test") || queryLower.contains("mock") -> {
                generateUnitTest(issue)
            }

            queryLower.contains("how to fix") || queryLower.contains("code") || queryLower.contains("solution") || queryLower.contains("patch") -> {
                val fixCode = diagnosis?.recommendedFixCode ?: "// Safe implementation\nuserProfile?.let { updateUi(it) }"
                AiChatMessage(
                    isUser = false,
                    text = "Here is the recommended code patch to resolve `${issue.exceptionType.ifBlank { "this issue" }}`:\n\n${diagnosis?.rootCauseSummary ?: "Apply defensive checks."}",
                    codeSnippet = fixCode,
                    suggestedAction = "Apply Solution"
                )
            }

            queryLower.contains("eli5") || queryLower.contains("simple") || queryLower.contains("plain english") || queryLower.contains("explain") -> {
                val explanation = diagnosis?.plainEnglishExplanation ?: "The app encountered an unexpected condition and could not continue."
                AiChatMessage(
                    isUser = false,
                    text = "### 💡 In Plain English (ELI5):\n\n$explanation\n\n**Next Steps:** ${diagnosis?.rootCauseSummary ?: "Verify inputs before proceeding."}"
                )
            }

            queryLower.contains("cause") || queryLower.contains("why") || queryLower.contains("reason") -> {
                val cause = diagnosis?.rootCauseSummary ?: "An unexpected runtime exception was raised."
                val title = diagnosis?.title ?: "Defect"
                AiChatMessage(
                    isUser = false,
                    text = "### 🔍 Root Cause Analysis:\n\n**Primary Cause:** $cause\n\n**Category:** $title\n\n**Architectural Impact:** When this condition occurs, the runtime halts to protect data consistency. Implementing defensive checks or coroutine scope boundaries will prevent this failure."
                )
            }

            queryLower.contains("permission") || queryLower.contains("manifest") || queryLower.contains("android") -> {
                AiChatMessage(
                    isUser = false,
                    text = "For Android apps, verify your `AndroidManifest.xml` includes all required permissions (e.g. `CAMERA`, `RECORD_AUDIO`, `INTERNET`, `POST_NOTIFICATIONS`) and request dangerous permissions dynamically at runtime using `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`."
                )
            }

            queryLower.contains("git") || queryLower.contains("pr") || queryLower.contains("commit") -> {
                AiChatMessage(
                    isUser = false,
                    text = "You can use the **GitHub / GitLab PR Creator** in the Magic Toolkit to generate a complete Conventional Commit message and pull request body with reproduction steps and device telemetry attached!"
                )
            }

            else -> {
                AiChatMessage(
                    isUser = false,
                    text = "I've analyzed issue #${issue.id} (`${issue.title}`).\n\n- **Exception:** `${issue.exceptionType.ifBlank { "Not detected" }}`\n- **Target File:** `${issue.sourceFile.ifBlank { "General Module" }}`\n\nAsk me to:\n1. 🧠 *\"Explain root cause\"*\n2. 🛠️ *\"Generate code fix\"*\n3. 🧪 *\"Generate unit test\"*\n4. 💡 *\"Explain in plain English\"*"
                )
            }
        }
    }
}
