package com.aitia.app.domain.model

/**
 * Relationships between issues:
 * Related to, Duplicate of, Blocked by, Caused by, Fixes.
 */
enum class RelationshipType(val displayName: String) {
    RELATED_TO("Related to"),
    DUPLICATE_OF("Duplicate of"),
    BLOCKED_BY("Blocked by"),
    CAUSED_BY("Caused by"),
    FIXES("Fixes");

    companion object {
        fun fromString(value: String?): RelationshipType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: RELATED_TO
        }
    }
}
