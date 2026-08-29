package com.aitia.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.aitia.app.domain.model.Issue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object BugCardRenderer {

    suspend fun renderAndSaveBugCard(context: Context, issue: Issue): File? = withContext(Dispatchers.IO) {
        try {
            val width = 1080
            val height = 1350
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Background: Pitch OLED Black
            canvas.drawColor(Color.parseColor("#080A0E"))

            // Neon Border Frame
            val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 6f
                color = Color.parseColor("#1F2937")
            }
            canvas.drawRoundRect(RectF(24f, 24f, width - 24f, height - 24f), 32f, 32f, framePaint)

            // Accent Top Glow Bar
            val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = Color.parseColor(
                    when (issue.priority.name) {
                        "CRITICAL" -> "#FF5252"
                        "HIGH" -> "#FFA726"
                        "MEDIUM" -> "#58A6FF"
                        else -> "#00FF88"
                    }
                )
            }
            canvas.drawRoundRect(RectF(24f, 24f, width - 24f, 36f), 12f, 12f, accentPaint)

            // Header: Aitia Logo / Title
            val logoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#BC8CFF")
                textSize = 38f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }
            canvas.drawText("AITIA (Αἰτία) · DEFECT TICKET #${issue.id}", 64f, 110f, logoPaint)

            // Priority & Type Badge
            val badgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = Color.parseColor("#161B22")
            }
            val badgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f
                color = accentPaint.color
            }
            val badgeRect = RectF(64f, 140f, 480f, 200f)
            canvas.drawRoundRect(badgeRect, 16f, 16f, badgeBgPaint)
            canvas.drawRoundRect(badgeRect, 16f, 16f, badgeBorderPaint)

            val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accentPaint.color
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("${issue.priority.name} · ${issue.type.displayName.uppercase()}", 84f, 182f, badgeTextPaint)

            // Status Pill
            val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#8B949E")
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("STATUS: ${issue.status.name}", 520f, 182f, statusPaint)

            // Issue Title (Wrapped)
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 46f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val titleLines = wrapText(issue.title, titlePaint, width - 128f)
            var yPos = 270f
            for (line in titleLines.take(3)) {
                canvas.drawText(line, 64f, yPos, titlePaint)
                yPos += 58f
            }

            // Project / Module Subtitle
            val projectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#58A6FF")
                textSize = 30f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            }
            val projInfo = "Project: ${issue.projectName ?: "Default"} · Screen: ${issue.screen.ifEmpty { "General" }}"
            canvas.drawText(projInfo, 64f, yPos + 10f, projectPaint)
            yPos += 70f

            // Separator line
            val dividerPaint = Paint().apply {
                color = Color.parseColor("#21262D")
                strokeWidth = 3f
            }
            canvas.drawLine(64f, yPos, width - 64f, yPos, dividerPaint)
            yPos += 45f

            // Technical Details / Stacktrace Card Section
            if (issue.exceptionType.isNotBlank() || issue.errorMessage.isNotBlank()) {
                val boxRect = RectF(64f, yPos, width - 64f, yPos + 220f)
                val codeBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#0D1117")
                }
                canvas.drawRoundRect(boxRect, 16f, 16f, codeBoxPaint)
                canvas.drawRoundRect(boxRect, 16f, 16f, framePaint)

                val exPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#FF6188")
                    textSize = 28f
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                canvas.drawText("EX: ${issue.exceptionType.ifEmpty { "UnknownException" }}", 90f, yPos + 50f, exPaint)

                val errPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#C9D1D9")
                    textSize = 24f
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                }
                val errLines = wrapText(issue.errorMessage.ifEmpty { issue.description }, errPaint, width - 180f)
                var errY = yPos + 100f
                for (line in errLines.take(3)) {
                    canvas.drawText(line, 90f, errY, errPaint)
                    errY += 36f
                }
                yPos += 250f
            } else {
                // Description block
                val descHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#8B949E")
                    textSize = 26f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("DESCRIPTION / REPRODUCTION:", 64f, yPos, descHeaderPaint)
                yPos += 36f

                val descPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#E6EDF3")
                    textSize = 26f
                }
                val descLines = wrapText(issue.stepsToReproduce.ifEmpty { issue.description.ifEmpty { "No reproduction steps provided." } }, descPaint, width - 128f)
                for (line in descLines.take(4)) {
                    canvas.drawText(line, 64f, yPos, descPaint)
                    yPos += 36f
                }
                yPos += 30f
            }

            // Solution / Root Cause if present
            if (issue.solution.isNotBlank() || issue.suspectedCause.isNotBlank()) {
                val fixHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#00FF88")
                    textSize = 26f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("ROOT CAUSE & FIX (Αἰτία):", 64f, yPos, fixHeaderPaint)
                yPos += 36f

                val fixPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#C9D1D9")
                    textSize = 24f
                }
                val fixLines = wrapText(if (issue.solution.isNotBlank()) issue.solution else issue.suspectedCause, fixPaint, width - 128f)
                for (line in fixLines.take(3)) {
                    canvas.drawText(line, 64f, yPos, fixPaint)
                    yPos += 34f
                }
                yPos += 30f
            }

            // Footer QR Code + Metadata
            val footerY = height - 260f
            canvas.drawLine(64f, footerY, width - 64f, footerY, dividerPaint)

            val qrContent = "aitia://quickcapture?issueId=${issue.id}&title=${issue.title.take(30)}"
            val qrBitmap = QrCodeGenerator.generateQrBitmap(
                content = qrContent,
                sizePx = 200,
                darkColor = Color.parseColor("#00F0FF")
            )
            if (qrBitmap != null) {
                canvas.drawBitmap(qrBitmap, width - 264f, footerY + 24f, null)
            }

            val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#8B949E")
                textSize = 24f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            }
            canvas.drawText("Generated with Aitia (Αἰτία)", 64f, footerY + 60f, metaPaint)
            canvas.drawText("Native Offline Developer Workstation", 64f, footerY + 100f, metaPaint)
            canvas.drawText("Privacy-First · 100% On-Device", 64f, footerY + 140f, metaPaint)
            canvas.drawText("Scan QR to inspect issue in Aitia", 64f, footerY + 180f, metaPaint)

            // Save to File
            val outDir = File(context.cacheDir, "bug_cards").apply { mkdirs() }
            val outFile = File(outDir, "aitia_card_${issue.id}.png")
            FileOutputStream(outFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            outFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }
}
