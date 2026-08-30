package com.aitia.app.domain.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ParsedStackTrace(
    val exceptionType: String? = null,
    val errorMessage: String? = null,
    val sourceFile: String? = null,
    val sourceLine: String? = null,
    val suggestedTitle: String? = null,
    val isParsed: Boolean = false
)

object StackTraceParser {

    private val EXCEPTION_REGEX = Regex(
        """(?:FATAL EXCEPTION|Exception in thread|Caused by:\s*)?([a-zA-Z0-9_.]+(?:Exception|Error|Throwable|Failure|Fault))(?::\s*(.*))?""",
        RegexOption.IGNORE_CASE
    )

    private val STACK_LINE_REGEX = Regex(
        """at\s+([a-zA-Z0-9_$.]+)\.([a-zA-Z0-9_$<>]+)\(([^:)]+\.(?:kt|java|c|cpp|dart|ts|js))(?::(\d+))?\)""",
        RegexOption.IGNORE_CASE
    )

    private val SHORT_STACK_REGEX = Regex(
        """([a-zA-Z0-9_]+\.(?:kt|java)):(\d+)""",
        RegexOption.IGNORE_CASE
    )

    suspend fun parse(rawText: String): ParsedStackTrace = withContext(Dispatchers.Default) {
        if (rawText.isBlank()) {
            return@withContext ParsedStackTrace()
        }

        var foundException: String? = null
        var foundMessage: String? = null
        var foundFile: String? = null
        var foundLine: String? = null

        val lines = rawText.lines()

        val frameworkPrefixes = listOf(
            "android.app.", "android.os.", "android.view.", "android.widget.",
            "java.lang.", "java.util.", "com.android.internal.", "dalvik.system.",
            "androidx.activity.", "androidx.fragment.app."
        )

        var candidateFrameworkFile: String? = null
        var candidateFrameworkLine: String? = null

        for (line in lines) {
            val trimmed = line.trim()

            // Try to match exception / error line
            if (foundException == null) {
                val exceptionMatch = EXCEPTION_REGEX.find(trimmed)
                if (exceptionMatch != null) {
                    val fullClass = exceptionMatch.groupValues[1].trim()
                    foundException = fullClass.substringAfterLast('.')
                    val message = exceptionMatch.groupValues.getOrNull(2)?.trim()
                    if (!message.isNullOrBlank()) {
                        foundMessage = message
                    }
                }
            }

            // Try to match standard stack trace line "at com.example.Foo.bar(Foo.kt:42)"
            val stackMatch = STACK_LINE_REGEX.find(trimmed)
            if (stackMatch != null) {
                val className = stackMatch.groupValues[1].trim()
                val fileName = stackMatch.groupValues[3].trim()
                val lineNum = stackMatch.groupValues.getOrNull(4)?.trim()

                val isFramework = frameworkPrefixes.any { className.startsWith(it) }
                if (!isFramework) {
                    if (foundFile == null) {
                        foundFile = fileName
                        foundLine = lineNum
                    }
                } else if (candidateFrameworkFile == null) {
                    candidateFrameworkFile = fileName
                    candidateFrameworkLine = lineNum
                }
            } else {
                // Fallback to "Foo.kt:42"
                val shortMatch = SHORT_STACK_REGEX.find(trimmed)
                if (shortMatch != null && foundFile == null) {
                    foundFile = shortMatch.groupValues[1].trim()
                    foundLine = shortMatch.groupValues[2].trim()
                }
            }
        }

        if (foundFile == null && candidateFrameworkFile != null) {
            foundFile = candidateFrameworkFile
            foundLine = candidateFrameworkLine
        }

        val suggestedTitle = when {
            foundException != null && foundFile != null && foundLine != null ->
                "$foundException in $foundFile:$foundLine"
            foundException != null && foundFile != null ->
                "$foundException in $foundFile"
            foundException != null && foundMessage != null ->
                "$foundException: ${foundMessage.take(40)}"
            foundException != null ->
                "Crash: $foundException"
            else -> null
        }

        val isParsed = foundException != null || foundFile != null || foundMessage != null

        return@withContext ParsedStackTrace(
            exceptionType = foundException,
            errorMessage = foundMessage,
            sourceFile = foundFile,
            sourceLine = foundLine,
            suggestedTitle = suggestedTitle,
            isParsed = isParsed
        )
    }
}
