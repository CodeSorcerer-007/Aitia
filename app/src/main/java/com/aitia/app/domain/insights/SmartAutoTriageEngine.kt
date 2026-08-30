package com.aitia.app.domain.insights

import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority

data class AutoTriageSuggestion(
    val suggestedType: IssueType,
    val suggestedPriority: Priority,
    val suggestedTags: List<String>,
    val suggestedInitialSteps: String
)

object SmartAutoTriageEngine {

    /**
     * Analyzes raw user title in real-time to infer defect type, priority, and relevant tags.
     */
    fun triage(title: String): AutoTriageSuggestion {
        val t = title.lowercase()

        // 1. Infer IssueType
        val type = when {
            t.contains("crash") || t.contains("fatal") || t.contains("exception") || t.contains("anr") || t.contains("force close") || t.contains("nullpointer") -> IssueType.CRASH
            t.contains("slow") || t.contains("lag") || t.contains("freeze") || t.contains("stutter") || t.contains("battery drain") || t.contains("hot") || t.contains("memory leak") -> IssueType.PERFORMANCE
            t.contains("align") || t.contains("cut off") || t.contains("overlap") || t.contains("color") || t.contains("font") || t.contains("dark mode") || t.contains("padding") || t.contains("ui") -> IssueType.UI_UX
            t.contains("leak") || t.contains("token") || t.contains("auth") || t.contains("password") || t.contains("permission") || t.contains("security") || t.contains("vulnerability") -> IssueType.SECURITY
            else -> IssueType.BUG
        }

        // 2. Infer Priority
        val priority = when {
            t.contains("crash on launch") || t.contains("fatal") || t.contains("payment") || t.contains("checkout") || t.contains("cannot login") || t.contains("data loss") -> Priority.CRITICAL
            t.contains("crash") || t.contains("freeze") || t.contains("broken") || t.contains("security") || t.contains("infinite loop") -> Priority.HIGH
            t.contains("slow") || t.contains("misaligned") || t.contains("typo") || t.contains("minor") -> Priority.LOW
            else -> Priority.MEDIUM
        }

        // 3. Infer Tags
        val tags = mutableListOf<String>()
        if (t.contains("camera") || t.contains("photo") || t.contains("video")) tags.add("camera")
        if (t.contains("network") || t.contains("api") || t.contains("timeout") || t.contains("curl") || t.contains("404") || t.contains("500")) tags.add("network")
        if (t.contains("login") || t.contains("signup") || t.contains("auth") || t.contains("session")) tags.add("auth")
        if (t.contains("pay") || t.contains("checkout") || t.contains("cart") || t.contains("billing")) tags.add("billing")
        if (t.contains("ui") || t.contains("button") || t.contains("dialog") || t.contains("compose") || t.contains("animation")) tags.add("ui")
        if (t.contains("database") || t.contains("room") || t.contains("sqlite") || t.contains("dao")) tags.add("database")
        if (t.contains("bluetooth") || t.contains("ble") || t.contains("wifi")) tags.add("hardware")

        // 4. Suggested Initial Steps
        val steps = when (type) {
            IssueType.CRASH -> "1. Open app\n2. Navigate to screen\n3. Trigger action: $title\n4. App terminates unexpectedly"
            IssueType.PERFORMANCE -> "1. Open app\n2. Perform continuous action\n3. Observe FPS drop or thermal rise"
            IssueType.UI_UX -> "1. Open screen\n2. Observe visual element layout\n3. Notice visual discrepancy"
            else -> "1. Open screen\n2. Perform action: $title\n3. Observe unexpected behavior"
        }

        return AutoTriageSuggestion(
            suggestedType = type,
            suggestedPriority = priority,
            suggestedTags = tags.distinct(),
            suggestedInitialSteps = steps
        )
    }
}
