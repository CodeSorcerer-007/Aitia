package com.aitia.app.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.CodeBackground

object MarkdownFormatter {

    @Composable
    fun formatMarkdown(text: String): AnnotatedString {
        val primaryColor = MaterialTheme.colorScheme.onSurface
        val codeBgColor = MaterialTheme.colorScheme.surfaceVariant

        return buildAnnotatedString {
            val lines = text.lines()
            for ((lineIdx, line) in lines.withIndex()) {
                var current = line

                // Bullet point prefix
                if (current.startsWith("* ") || current.startsWith("- ")) {
                    withStyle(SpanStyle(color = AitiaBlue, fontWeight = FontWeight.Bold)) {
                        append(" • ")
                    }
                    current = current.drop(2)
                }

                // Numbered list prefix
                val numMatch = Regex("""^(\d+\.)\s*(.*)""").find(current)
                if (numMatch != null) {
                    val num = numMatch.groupValues[1]
                    withStyle(SpanStyle(color = AitiaBlue, fontWeight = FontWeight.Bold)) {
                        append("$num ")
                    }
                    current = numMatch.groupValues[2]
                }

                // Bold and Inline Code parsing
                var i = 0
                while (i < current.length) {
                    when {
                        // Bold **text**
                        current.startsWith("**", i) -> {
                            val end = current.indexOf("**", i + 2)
                            if (end != -1) {
                                val boldText = current.substring(i + 2, end)
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryColor)) {
                                    append(boldText)
                                }
                                i = end + 2
                            } else {
                                append(current[i])
                                i++
                            }
                        }
                        // Inline Code `code`
                        current.startsWith("`", i) -> {
                            val end = current.indexOf("`", i + 1)
                            if (end != -1) {
                                val codeText = current.substring(i + 1, end)
                                withStyle(
                                    SpanStyle(
                                        fontFamily = FontFamily.Monospace,
                                        background = codeBgColor,
                                        color = AitiaBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append(" $codeText ")
                                }
                                i = end + 1
                            } else {
                                append(current[i])
                                i++
                            }
                        }
                        else -> {
                            append(current[i])
                            i++
                        }
                    }
                }

                if (lineIdx < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }
}
