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

    val isAudio: Boolean
        get() = mimeType.startsWith("audio/") || filename.endsWith(".m4a") || filename.endsWith(".mp4") || filename.endsWith(".mp3")

    val isLogOrText: Boolean
        get() = mimeType.startsWith("text/") || filename.endsWith(".log") || filename.endsWith(".txt")

    val formattedSize: String
        get() = if (sizeBytes > 1024 * 1024) {
            "${sizeBytes / (1024 * 1024)} MB"
        } else {
            "${(sizeBytes / 1024).coerceAtLeast(1)} KB"
        }
}
