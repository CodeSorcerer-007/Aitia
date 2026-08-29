package com.aitia.app.domain.model

data class ChecklistItem(
    val id: Long = 0,
    val issueId: Long,
    val text: String,
    val isCompleted: Boolean = false,
    val position: Int = 0
)
