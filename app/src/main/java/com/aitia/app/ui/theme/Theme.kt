package com.aitia.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.aitia.app.domain.model.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = AitiaBlue,
    onPrimary = DarkBackground,
    primaryContainer = AitiaBlueMuted,
    onPrimaryContainer = Color.White,
    secondary = AitiaPurple,
    onSecondary = DarkBackground,
    secondaryContainer = AitiaPurpleMuted,
    onSecondaryContainer = Color.White,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle,
    error = StatusBlocked,
    onError = Color.White
)

private val OledColorScheme = darkColorScheme(
    primary = AitiaBlue,
    onPrimary = Color.Black,
    primaryContainer = AitiaBlueMuted,
    onPrimaryContainer = Color.White,
    secondary = AitiaPurple,
    onSecondary = Color.Black,
    secondaryContainer = AitiaPurpleMuted,
    onSecondaryContainer = Color.White,
    background = Color.Black,
    onBackground = TextPrimaryDark,
    surface = Color(0xFF0F1218),
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF171B24),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF242A35),
    outlineVariant = Color(0xFF1B202A),
    error = StatusBlocked,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0969DA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF4FF),
    onPrimaryContainer = Color(0xFF0969DA),
    secondary = Color(0xFF8250DF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBEFFF),
    onSecondaryContainer = Color(0xFF8250DF),
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = LightBorderSubtle,
    error = StatusBlocked,
    onError = Color.White
)

@Immutable
data class ExtendedColors(
    val statusOpen: Color = StatusOpen,
    val statusInvestigating: Color = StatusInvestigating,
    val statusBlocked: Color = StatusBlocked,
    val statusFixed: Color = StatusFixed,
    val statusVerified: Color = StatusVerified,
    val statusClosed: Color = StatusClosed,
    val priorityLow: Color = PriorityLow,
    val priorityMedium: Color = PriorityMedium,
    val priorityHigh: Color = PriorityHigh,
    val priorityCritical: Color = PriorityCritical,
    val codeBackground: Color = CodeBackground,
    val textTertiary: Color = TextTertiaryDark
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }

@Composable
fun AitiaTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.SYSTEM -> if (isSystemDark) DarkColorScheme else LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.OLED -> OledColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
    }

    val extendedColors = ExtendedColors(
        textTertiary = if (colorScheme == LightColorScheme) TextTertiaryLight else TextTertiaryDark
    )

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
