package com.aitia.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val isRibbon: Boolean
)

@Composable
fun ConfettiEffect(
    trigger: Boolean,
    onAnimationEnd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val progress = remember { Animatable(0f) }
    val colors = listOf(
        Color(0xFF00E5FF), // Cyan
        Color(0xFF00FF88), // Neon Green
        Color(0xFF8B5CF6), // Purple
        Color(0xFFFFB703), // Gold
        Color(0xFFFF3366), // Pink
        Color(0xFF38BDF8)  // Blue
    )

    val particles = remember {
        List(90) {
            val angle = Random.nextDouble(0.0, Math.PI * 2)
            val speed = Random.nextDouble(400.0, 1400.0).toFloat()
            Particle(
                x = 0.5f,
                y = 0.4f,
                velocityX = (cos(angle) * speed).toFloat(),
                velocityY = (sin(angle) * speed - 300f).toFloat(),
                color = colors.random(),
                size = Random.nextDouble(12.0, 26.0).toFloat(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextDouble(-720.0, 720.0).toFloat(),
                isRibbon = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
        onAnimationEnd()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val t = progress.value
            val gravity = 980f * t * t
            val alpha = (1f - t).coerceIn(0f, 1f)

            particles.forEach { p ->
                val px = size.width * p.x + p.velocityX * t
                val py = size.height * p.y + p.velocityY * t + gravity
                val currentRot = p.rotation + p.rotationSpeed * t

                if (px in -50f..(size.width + 50f) && py in -50f..(size.height + 50f)) {
                    rotate(degrees = currentRot, pivot = Offset(px, py)) {
                        if (p.isRibbon) {
                            drawRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = Offset(px - p.size / 2, py - p.size / 4),
                                size = Size(p.size, p.size / 2.5f)
                            )
                        } else {
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.size / 3f,
                                center = Offset(px, py)
                            )
                        }
                    }
                }
            }
        }
    }
}
