package com.aitia.app.domain.model

import java.time.Instant

data class Attachment(
    val id: Long = 0,
    val issueId: Long,
    val uriPath: String,
    val filename: String,
    val mimeType: String = "image/png",
    val sizeBytes: Long = 0,
    val createdAt: Instant = Instant.now()
) {
    val isImage: Boolean
        get() = mimeType.startsWith("image/")

    val isVideo: Boolean
        get() = mimeType.startsWith("video/")

    val isLogOrText: Boolean
        get() = mimeType.startsWith("text/") || filename.endsWith(".log") || filename.endsWith(".txt")
}
