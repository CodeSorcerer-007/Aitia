package com.aitia.app.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File
import java.io.FileOutputStream
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class AnnotationTool {
    PEN, ARROW, RECTANGLE, REDACT_BLUR, TEXT
}

sealed class AnnotationAction {
    data class Freehand(val points: List<Offset>, val color: Color, val strokeWidth: Float) : AnnotationAction()
    data class Arrow(val start: Offset, val end: Offset, val color: Color, val strokeWidth: Float) : AnnotationAction()
    data class Rectangle(val start: Offset, val end: Offset, val color: Color, val strokeWidth: Float) : AnnotationAction()
    data class Redact(val start: Offset, val end: Offset) : AnnotationAction()
    data class TextNote(val position: Offset, val text: String, val color: Color) : AnnotationAction()
}

@Composable
fun ImageAnnotationDialog(
    imageFile: File,
    onDismiss: () -> Unit,
    onSaveAnnotatedImage: (File) -> Unit
) {
    val bitmap = remember(imageFile) {
        BitmapFactory.decodeFile(imageFile.absolutePath)
    }

    if (bitmap == null) {
        onDismiss()
        return
    }

    var selectedTool by remember { mutableStateOf(AnnotationTool.PEN) }
    var selectedColor by remember { mutableStateOf(Color(0xFFFF5252)) }
    var isMoireFilterEnabled by remember { mutableStateOf(false) }

    val actions = remember { mutableStateListOf<AnnotationAction>() }
    var currentDragStart by remember { mutableStateOf<Offset?>(null) }
    var currentDragEnd by remember { mutableStateOf<Offset?>(null) }
    val currentPoints = remember { mutableStateListOf<Offset>() }

    val palette = listOf(
        Color(0xFFFF5252), // Neon Red
        Color(0xFF00FF88), // Emerald Green
        Color(0xFF00F0FF), // Laser Cyan
        Color(0xFFBC8CFF), // Αἰτία Purple
        Color(0xFFFFD600), // Yellow
        Color(0xFFFFFFFF)  // White
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B22))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }

                    Text(
                        text = "Bug Markup & Redact",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Row {
                        IconButton(
                            onClick = { if (actions.isNotEmpty()) actions.removeAt(actions.size - 1) },
                            enabled = actions.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                tint = if (actions.isNotEmpty()) Color.White else Color.Gray
                            )
                        }
                        IconButton(
                            onClick = {
                                val annotated = saveAnnotatedBitmap(bitmap, actions, isMoireFilterEnabled, imageFile)
                                onSaveAnnotatedImage(annotated)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Save", tint = Color(0xFF00FF88))
                        }
                    }
                }

                // Interactive Canvas Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp))
                        .pointerInput(selectedTool, selectedColor) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentDragStart = offset
                                    currentDragEnd = offset
                                    if (selectedTool == AnnotationTool.PEN) {
                                        currentPoints.clear()
                                        currentPoints.add(offset)
                                    }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentDragEnd = change.position
                                    if (selectedTool == AnnotationTool.PEN) {
                                        currentPoints.add(change.position)
                                    }
                                },
                                onDragEnd = {
                                    val start = currentDragStart
                                    val end = currentDragEnd
                                    if (start != null && end != null) {
                                        when (selectedTool) {
                                            AnnotationTool.PEN -> {
                                                if (currentPoints.isNotEmpty()) {
                                                    actions.add(AnnotationAction.Freehand(currentPoints.toList(), selectedColor, 8f))
                                                }
                                            }
                                            AnnotationTool.ARROW -> {
                                                actions.add(AnnotationAction.Arrow(start, end, selectedColor, 8f))
                                            }
                                            AnnotationTool.RECTANGLE -> {
                                                actions.add(AnnotationAction.Rectangle(start, end, selectedColor, 6f))
                                            }
                                            AnnotationTool.REDACT_BLUR -> {
                                                actions.add(AnnotationAction.Redact(start, end))
                                            }
                                            AnnotationTool.TEXT -> {
                                                actions.add(AnnotationAction.TextNote(end, "BUG HERE", selectedColor))
                                            }
                                        }
                                    }
                                    currentDragStart = null
                                    currentDragEnd = null
                                    currentPoints.clear()
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val imageBitmap = bitmap.asImageBitmap()
                        drawImage(
                            image = imageBitmap,
                            dstSize = androidx.compose.ui.unit.IntSize(canvasWidth.toInt(), canvasHeight.toInt())
                        )

                        // Draw Committed Actions
                        actions.forEach { action ->
                            when (action) {
                                is AnnotationAction.Freehand -> {
                                    for (i in 0 until action.points.size - 1) {
                                        drawLine(
                                            color = action.color,
                                            start = action.points[i],
                                            end = action.points[i + 1],
                                            strokeWidth = action.strokeWidth,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    }
                                }
                                is AnnotationAction.Arrow -> {
                                    drawArrow(action.start, action.end, action.color, action.strokeWidth)
                                }
                                is AnnotationAction.Rectangle -> {
                                    val left = minOf(action.start.x, action.end.x)
                                    val top = minOf(action.start.y, action.end.y)
                                    val right = maxOf(action.start.x, action.end.x)
                                    val bottom = maxOf(action.start.y, action.end.y)
                                    drawRect(
                                        color = action.color,
                                        topLeft = Offset(left, top),
                                        size = Size(right - left, bottom - top),
                                        style = Stroke(width = action.strokeWidth)
                                    )
                                }
                                is AnnotationAction.Redact -> {
                                    val left = minOf(action.start.x, action.end.x)
                                    val top = minOf(action.start.y, action.end.y)
                                    val right = maxOf(action.start.x, action.end.x)
                                    val bottom = maxOf(action.start.y, action.end.y)
                                    drawRect(
                                        color = Color.Black.copy(alpha = 0.92f),
                                        topLeft = Offset(left, top),
                                        size = Size(right - left, bottom - top)
                                    )
                                }
                                is AnnotationAction.TextNote -> {
                                    // Text placeholder drawing
                                    drawCircle(color = action.color, radius = 12f, center = action.position)
                                }
                            }
                        }

                        // Draw Active Dragging Action
                        val start = currentDragStart
                        val end = currentDragEnd
                        if (start != null && end != null) {
                            when (selectedTool) {
                                AnnotationTool.PEN -> {
                                    for (i in 0 until currentPoints.size - 1) {
                                        drawLine(
                                            color = selectedColor,
                                            start = currentPoints[i],
                                            end = currentPoints[i + 1],
                                            strokeWidth = 8f,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    }
                                }
                                AnnotationTool.ARROW -> drawArrow(start, end, selectedColor, 8f)
                                AnnotationTool.RECTANGLE -> {
                                    val left = minOf(start.x, end.x)
                                    val top = minOf(start.y, end.y)
                                    val right = maxOf(start.x, end.x)
                                    val bottom = maxOf(start.y, end.y)
                                    drawRect(
                                        color = selectedColor,
                                        topLeft = Offset(left, top),
                                        size = Size(right - left, bottom - top),
                                        style = Stroke(width = 6f)
                                    )
                                }
                                AnnotationTool.REDACT_BLUR -> {
                                    val left = minOf(start.x, end.x)
                                    val top = minOf(start.y, end.y)
                                    val right = maxOf(start.x, end.x)
                                    val bottom = maxOf(start.y, end.y)
                                    drawRect(
                                        color = Color.Black.copy(alpha = 0.8f),
                                        topLeft = Offset(left, top),
                                        size = Size(right - left, bottom - top)
                                    )
                                }
                                AnnotationTool.TEXT -> {
                                    drawCircle(color = selectedColor, radius = 14f, center = end)
                                }
                            }
                        }
                    }
                }

                // Bottom Tool & Color Selector
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B22))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Palette & Screen Filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            palette.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (selectedColor == color) 3.dp else 1.dp,
                                            color = if (selectedColor == color) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = color }
                                )
                            }
                        }

                        // Moire Filter Toggle
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isMoireFilterEnabled) Color(0xFF00FF88).copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { isMoireFilterEnabled = !isMoireFilterEnabled }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Moire Filter",
                                tint = if (isMoireFilterEnabled) Color(0xFF00FF88) else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Anti-Moire",
                                fontSize = 12.sp,
                                color = if (isMoireFilterEnabled) Color(0xFF00FF88) else Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tool Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ToolIconButton(
                            icon = Icons.Default.Edit,
                            label = "Pen",
                            isSelected = selectedTool == AnnotationTool.PEN,
                            onClick = { selectedTool = AnnotationTool.PEN }
                        )
                        ToolIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            label = "Arrow",
                            isSelected = selectedTool == AnnotationTool.ARROW,
                            onClick = { selectedTool = AnnotationTool.ARROW }
                        )
                        ToolIconButton(
                            icon = Icons.Default.CropSquare,
                            label = "Box",
                            isSelected = selectedTool == AnnotationTool.RECTANGLE,
                            onClick = { selectedTool = AnnotationTool.RECTANGLE }
                        )
                        ToolIconButton(
                            icon = Icons.Default.VisibilityOff,
                            label = "Redact",
                            isSelected = selectedTool == AnnotationTool.REDACT_BLUR,
                            onClick = { selectedTool = AnnotationTool.REDACT_BLUR }
                        )
                        ToolIconButton(
                            icon = Icons.Default.TextFields,
                            label = "Text",
                            isSelected = selectedTool == AnnotationTool.TEXT,
                            onClick = { selectedTool = AnnotationTool.TEXT }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF30363D) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF00F0FF) else Color(0xFF8B949E),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) Color.White else Color(0xFF8B949E)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArrow(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float
) {
    drawLine(color = color, start = start, end = end, strokeWidth = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    val angle = atan2((end.y - start.y).toDouble(), (end.x - start.x).toDouble())
    val arrowHeadLength = 32f
    val arrowAngle = Math.PI / 6.0

    val p1 = Offset(
        (end.x - arrowHeadLength * cos(angle - arrowAngle)).toFloat(),
        (end.y - arrowHeadLength * sin(angle - arrowAngle)).toFloat()
    )
    val p2 = Offset(
        (end.x - arrowHeadLength * cos(angle + arrowAngle)).toFloat(),
        (end.y - arrowHeadLength * sin(angle + arrowAngle)).toFloat()
    )
    drawLine(color = color, start = end, end = p1, strokeWidth = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawLine(color = color, start = end, end = p2, strokeWidth = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
}

private fun saveAnnotatedBitmap(
    sourceBitmap: Bitmap,
    actions: List<AnnotationAction>,
    antiMoire: Boolean,
    originalFile: File
): File {
    val mutableBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)

    val scaleX = mutableBitmap.width.toFloat() / 1080f
    val scaleY = mutableBitmap.height.toFloat() / 1920f

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    actions.forEach { action ->
        when (action) {
            is AnnotationAction.Freehand -> {
                paint.color = action.color.toArgb()
                paint.strokeWidth = action.strokeWidth * scaleX
                paint.style = Paint.Style.STROKE
                val path = Path()
                if (action.points.isNotEmpty()) {
                    path.moveTo(action.points.first().x * scaleX, action.points.first().y * scaleY)
                    for (pt in action.points) {
                        path.lineTo(pt.x * scaleX, pt.y * scaleY)
                    }
                    canvas.drawPath(path, paint)
                }
            }
            is AnnotationAction.Arrow -> {
                paint.color = action.color.toArgb()
                paint.strokeWidth = action.strokeWidth * scaleX
                paint.style = Paint.Style.STROKE
                val startX = action.start.x * scaleX
                val startY = action.start.y * scaleY
                val endX = action.end.x * scaleX
                val endY = action.end.y * scaleY
                canvas.drawLine(startX, startY, endX, endY, paint)

                val angle = atan2((endY - startY).toDouble(), (endX - startX).toDouble())
                val arrowHeadLen = 40f * scaleX
                val arrowAngle = Math.PI / 6.0
                val p1X = (endX - arrowHeadLen * cos(angle - arrowAngle)).toFloat()
                val p1Y = (endY - arrowHeadLen * sin(angle - arrowAngle)).toFloat()
                val p2X = (endX - arrowHeadLen * cos(angle + arrowAngle)).toFloat()
                val p2Y = (endY - arrowHeadLen * sin(angle + arrowAngle)).toFloat()
                canvas.drawLine(endX, endY, p1X, p1Y, paint)
                canvas.drawLine(endX, endY, p2X, p2Y, paint)
            }
            is AnnotationAction.Rectangle -> {
                paint.color = action.color.toArgb()
                paint.strokeWidth = action.strokeWidth * scaleX
                paint.style = Paint.Style.STROKE
                val left = minOf(action.start.x, action.end.x) * scaleX
                val top = minOf(action.start.y, action.end.y) * scaleY
                val right = maxOf(action.start.x, action.end.x) * scaleX
                val bottom = maxOf(action.start.y, action.end.y) * scaleY
                canvas.drawRect(left, top, right, bottom, paint)
            }
            is AnnotationAction.Redact -> {
                val redactPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = AndroidColor.BLACK
                    style = Paint.Style.FILL
                }
                val left = minOf(action.start.x, action.end.x) * scaleX
                val top = minOf(action.start.y, action.end.y) * scaleY
                val right = maxOf(action.start.x, action.end.x) * scaleX
                val bottom = maxOf(action.start.y, action.end.y) * scaleY
                canvas.drawRect(left, top, right, bottom, redactPaint)
            }
            is AnnotationAction.TextNote -> {
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = action.color.toArgb()
                    textSize = 40f * scaleX
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                canvas.drawText("BUG: ${action.text}", action.position.x * scaleX, action.position.y * scaleY, textPaint)
            }
        }
    }

    val outputFile = File(originalFile.parentFile, "annotated_${System.currentTimeMillis()}.png")
    FileOutputStream(outputFile).use { fos ->
        mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }
    return outputFile
}
