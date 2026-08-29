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

// 1. OLED Midnight Obsidian (Electric Blue / Linear Pro)
private val OledMidnightColorScheme = darkColorScheme(
    primary = OledMidnightBlue,
    onPrimary = OledBlack,
    primaryContainer = Color(0xFF102844),
    onPrimaryContainer = Color(0xFFDDF4FF),
    secondary = OledMidnightCyan,
    onSecondary = OledBlack,
    secondaryContainer = Color(0xFF0C2438),
    onSecondaryContainer = Color(0xFFC7EBFF),
    background = OledBlack,
    onBackground = TextPrimaryDark,
    surface = OledMidnightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = OledMidnightSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = OledMidnightBorder,
    outlineVariant = Color(0xFF131D2D),
    error = StatusBlocked,
    onError = Color.White
)

// 2. OLED Cyberpunk Matrix (Emerald Matrix & Laser Cyan)
private val OledCyberpunkColorScheme = darkColorScheme(
    primary = OledCyberGreen,
    onPrimary = OledBlack,
    primaryContainer = Color(0xFF082614),
    onPrimaryContainer = Color(0xFFB8FFD8),
    secondary = OledCyberCyan,
    onSecondary = OledBlack,
    secondaryContainer = Color(0xFF06242B),
    onSecondaryContainer = Color(0xFFB5F8FF),
    background = OledBlack,
    onBackground = Color(0xFFF0FFF4),
    surface = OledCyberSurface,
    onSurface = Color(0xFFF0FFF4),
    surfaceVariant = OledCyberSurfaceVariant,
    onSurfaceVariant = Color(0xFF86A894),
    outline = OledCyberBorder,
    outlineVariant = Color(0xFF0F2618),
    error = Color(0xFFFF2A6D),
    onError = Color.White
)

// 3. OLED Nebula Amethyst (Greek Αἰτία Royal Purple & Magenta)
private val OledNebulaColorScheme = darkColorScheme(
    primary = OledNebulaPurple,
    onPrimary = OledBlack,
    primaryContainer = Color(0xFF26123D),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = OledNebulaMagenta,
    onSecondary = OledBlack,
    secondaryContainer = Color(0xFF331024),
    onSecondaryContainer = Color(0xFFFFE4F2),
    background = OledBlack,
    onBackground = Color(0xFFFAF5FF),
    surface = OledNebulaSurface,
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = OledNebulaSurfaceVariant,
    onSurfaceVariant = Color(0xFFA79BBA),
    outline = OledNebulaBorder,
    outlineVariant = Color(0xFF1C1333),
    error = StatusBlocked,
    onError = Color.White
)

// 4. OLED Tokyo Monokai (Vivid Multi-color Code Syntax)
private val OledMonokaiColorScheme = darkColorScheme(
    primary = OledMonokaiPink,
    onPrimary = OledBlack,
    primaryContainer = Color(0xFF3B121E),
    onPrimaryContainer = Color(0xFFFFDDE6),
    secondary = OledMonokaiOrange,
    onSecondary = OledBlack,
    secondaryContainer = Color(0xFF3A1E11),
    onSecondaryContainer = Color(0xFFFFE7DD),
    background = OledBlack,
    onBackground = Color(0xFFFDFDFD),
    surface = OledMonokaiSurface,
    onSurface = Color(0xFFFDFDFD),
    surfaceVariant = OledMonokaiSurfaceVariant,
    onSurfaceVariant = Color(0xFF9EA3B0),
    outline = OledMonokaiBorder,
    outlineVariant = Color(0xFF1C1E26),
    error = Color(0xFFFF5555),
    onError = Color.White
)

// 5. Classic GitHub Dark Slate
private val DarkSlateColorScheme = darkColorScheme(
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

// 6. Light Clean Daylight
private val LightCleanColorScheme = lightColorScheme(
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
    themeMode: AppThemeMode = AppThemeMode.OLED_MIDNIGHT,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()

    val colorScheme = when (themeMode) {
        AppThemeMode.OLED_MIDNIGHT -> OledMidnightColorScheme
        AppThemeMode.OLED_CYBERPUNK -> OledCyberpunkColorScheme
        AppThemeMode.OLED_AMETHYST -> OledNebulaColorScheme
        AppThemeMode.OLED_MONOKAI -> OledMonokaiColorScheme
        AppThemeMode.DARK_SLATE -> DarkSlateColorScheme
        AppThemeMode.LIGHT_CLEAN -> LightCleanColorScheme
        AppThemeMode.SYSTEM -> if (isSystemDark) OledMidnightColorScheme else LightCleanColorScheme
    }

    val extendedColors = when (themeMode) {
        AppThemeMode.OLED_CYBERPUNK -> ExtendedColors(
            statusOpen = OledCyberCyan,
            statusFixed = OledCyberGreen,
            priorityCritical = Color(0xFFFF2A6D),
            codeBackground = OledCyberCodeBg,
            textTertiary = Color(0xFF638270)
        )
        AppThemeMode.OLED_AMETHYST -> ExtendedColors(
            statusOpen = OledNebulaPurple,
            statusFixed = Color(0xFF4ADE80),
            priorityCritical = Color(0xFFFB7185),
            codeBackground = OledNebulaCodeBg,
            textTertiary = Color(0xFF7E7394)
        )
        AppThemeMode.OLED_MONOKAI -> ExtendedColors(
            statusOpen = OledMonokaiCyan,
            statusFixed = OledMonokaiGreen,
            priorityCritical = Color(0xFFFF5555),
            codeBackground = OledMonokaiCodeBg,
            textTertiary = Color(0xFF757A88)
        )
        AppThemeMode.OLED_MIDNIGHT -> ExtendedColors(
            statusOpen = OledMidnightBlue,
            statusFixed = Color(0xFF3FB950),
            codeBackground = OledMidnightCodeBg,
            textTertiary = TextTertiaryDark
        )
        AppThemeMode.LIGHT_CLEAN -> ExtendedColors(
            textTertiary = TextTertiaryLight
        )
        else -> ExtendedColors(
            textTertiary = if (colorScheme == LightCleanColorScheme) TextTertiaryLight else TextTertiaryDark
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
