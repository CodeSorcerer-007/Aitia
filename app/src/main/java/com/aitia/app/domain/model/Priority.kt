package com.aitia.app.domain.model

/**
 * Priorities according to Aitia Master Spec:
 * Low, Medium, High, Critical.
 */
enum class Priority(
    val displayName: String,
    val severityLevel: Int,
    val description: String
) {
    LOW("Low", 1, "Minor problem or cosmetic defect"),
    MEDIUM("Medium", 2, "Significant problem with known workaround"),
    HIGH("High", 3, "Major functionality broken or failing"),
    CRITICAL("Critical", 4, "Application unusable, severe data loss, or crash");

    companion object {
        fun fromString(value: String?): Priority {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}
