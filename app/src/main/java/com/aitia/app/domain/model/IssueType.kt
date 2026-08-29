package com.aitia.app.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Compact list of issue types according to Aitia Master Spec:
 * Bug, Crash, Error, UI/UX, Performance, Security, Test Observation, Feature/Improvement, Other.
 */
enum class IssueType(
    val displayName: String,
    val description: String
) {
    BUG("Bug", "General functional bug or logic defect"),
    CRASH("Crash", "Fatal exception, ANR, or app termination"),
    ERROR("Error", "Handled error, network failure, or API failure"),
    UI_UX("UI / UX", "Visual glitch, alignment, theme, or UX flaw"),
    PERFORMANCE("Performance", "Slow rendering, memory leak, or high latency"),
    SECURITY("Security", "Vulnerability, permission flaw, or data leak"),
    TEST_OBSERVATION("Test Observation", "Observation from QA or exploratory testing"),
    FEATURE_IMPROVEMENT("Feature / Improvement", "Enhancement or feature idea discovered while debugging"),
    OTHER("Other", "Miscellaneous engineering note");

    companion object {
        fun fromString(value: String?): IssueType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: BUG
        }
    }
}
