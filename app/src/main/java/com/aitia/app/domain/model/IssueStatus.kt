package com.aitia.app.domain.model

/**
 * Issue statuses according to Aitia Master Spec:
 * Open, Investigating, Blocked, Fixed, Verified, Closed.
 */
enum class IssueStatus(
    val displayName: String,
    val order: Int
) {
    OPEN("Open", 0),
    INVESTIGATING("Investigating", 1),
    BLOCKED("Blocked", 2),
    FIXED("Fixed", 3),
    VERIFIED("Verified", 4),
    CLOSED("Closed", 5);

    val isResolved: Boolean
        get() = this == FIXED || this == VERIFIED || this == CLOSED

    companion object {
        fun fromString(value: String?): IssueStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OPEN
        }
    }
}
