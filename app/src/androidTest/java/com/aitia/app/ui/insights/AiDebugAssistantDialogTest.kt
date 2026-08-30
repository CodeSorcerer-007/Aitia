package com.aitia.app.ui.insights

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aitia.app.domain.insights.DebugSuggestion
import com.aitia.app.ui.components.AiDebugAssistantDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class AiDebugAssistantDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogDisplaysLoadingState() {
        composeTestRule.setContent {
            AiDebugAssistantDialog(
                isLoading = true,
                suggestions = emptyList(),
                error = null,
                onDismiss = {},
                onQuery = {}
            )
        }

        composeTestRule.onNodeWithText("Analyzing...").assertExists()
    }

    @Test
    fun dialogDisplaysSuggestions() {
        val testSuggestions = listOf(
            DebugSuggestion(
                id = "1",
                title = "Check Nullability",
                description = "Variable might be null",
                confidenceScore = 0.9f,
                actionableSteps = listOf("Check if null")
            )
        )

        composeTestRule.setContent {
            AiDebugAssistantDialog(
                isLoading = false,
                suggestions = testSuggestions,
                error = null,
                onDismiss = {},
                onQuery = {}
            )
        }

        composeTestRule.onNodeWithText("Check Nullability").assertExists()
        composeTestRule.onNodeWithText("Variable might be null").assertExists()
    }

    @Test
    fun queryingTriggersCallback() {
        var queryText = ""
        
        composeTestRule.setContent {
            AiDebugAssistantDialog(
                isLoading = false,
                suggestions = emptyList(),
                error = null,
                onDismiss = {},
                onQuery = { queryText = it }
            )
        }

        composeTestRule.onNodeWithText("Ask a follow-up question...").performTextInput("Why did it fail?")
        composeTestRule.onNodeWithText("Send").performClick()

        assertEquals("Why did it fail?", queryText)
    }
}
