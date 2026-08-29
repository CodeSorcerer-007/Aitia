package com.aitia.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.R
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.AitiaPurple

/**
 * Modern, branded Aitia logo with smooth breathing pulse and rotating halo when loading.
 */
@Composable
fun AitiaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isLoading: Boolean = false,
    cornerRadius: Dp = 12.dp
) {
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition(label = "AitiaLogoPulse")

        // Smooth breathing scale
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "AitiaLogoScale"
        )

        // Continuous aura rotation
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "AitiaLogoRotation"
        )

        // Ambient glow alpha
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "AitiaLogoGlowAlpha"
        )

        Box(
            modifier = modifier.size(size * 1.35f),
            contentAlignment = Alignment.Center
        ) {
            // Rotating neon gradient aura
            Box(
                modifier = Modifier
                    .size(size * 1.25f)
                    .scale(scale)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .drawBehind {
                        val brush = Brush.sweepGradient(
                            listOf(
                                AitiaBlue.copy(alpha = glowAlpha),
                                AitiaPurple.copy(alpha = glowAlpha * 0.8f),
                                Color(0xFF3FB950).copy(alpha = glowAlpha * 0.5f),
                                AitiaBlue.copy(alpha = glowAlpha)
                            )
                        )
                        drawCircle(
                            brush = brush,
                            style = Stroke(width = 3.5.dp.toPx())
                        )
                    }
            )

            // Logo with subtle scale
            Image(
                painter = painterResource(id = R.drawable.aitia_logo),
                contentDescription = "Aitia Loading",
                modifier = Modifier
                    .size(size)
                    .scale(scale)
                    .clip(RoundedCornerShape(cornerRadius))
                    .border(1.dp, AitiaBlue.copy(alpha = 0.4f), RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        // Crisp static display
        Image(
            painter = painterResource(id = R.drawable.aitia_logo),
            contentDescription = "Aitia Logo",
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(cornerRadius)),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Full-screen or modal animated loading view with Aitia logo and animated status message.
 */
@Composable
fun AitiaLoadingScreen(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AitiaLogo(
                size = 64.dp,
                isLoading = true,
                cornerRadius = 16.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Αἰτία · Finding the cause",
                style = MaterialTheme.typography.labelSmall,
                color = AitiaBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
