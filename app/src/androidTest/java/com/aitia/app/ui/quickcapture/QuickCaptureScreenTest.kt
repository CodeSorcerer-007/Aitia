package com.aitia.app.ui.quickcapture

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsEnabled
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.flow.MutableStateFlow

class QuickCaptureScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testQuickCaptureDialog_saveButtonDisabledWhenTitleEmpty() {
        val mockViewModel = object : QuickCaptureViewModel() {
            // Mocking simple state for the test
            override val uiState = MutableStateFlow(QuickCaptureUiState())
            override fun onTitleChange(newTitle: String) {
                uiState.value = uiState.value.copy(title = newTitle)
            }
        }

        composeTestRule.setContent {
            QuickCaptureBottomSheet(
                viewModel = mockViewModel,
                onDismiss = {},
                onIssueCreated = {},
                onOpenExistingIssue = {}
            )
        }

        // Verify the title is present
        composeTestRule.onNodeWithText("Quick Capture").assertIsDisplayed()

        // Verify save button is initially disabled
        composeTestRule.onNodeWithText("Save & Continue Testing").assertIsNotEnabled()

        // Type something in the "What happened?" field
        composeTestRule.onNodeWithText("What happened? (e.g. Camera crashes when changing photo)").performTextInput("Test issue title")

        // Verify save button is now enabled
        composeTestRule.onNodeWithText("Save & Continue Testing").assertIsEnabled()
    }
}
