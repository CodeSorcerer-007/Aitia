package com.aitia.app.domain.model

enum class AppThemeMode(
    val displayName: String,
    val description: String,
    val isOled: Boolean,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val surfaceColorHex: String
) {
    OLED_MIDNIGHT(
        displayName = "Obsidian Blue",
        description = "Pitch OLED black with electric blue neon lines",
        isOled = true,
        primaryColorHex = "#58A6FF",
        secondaryColorHex = "#388BFD",
        surfaceColorHex = "#0D1117"
    ),
    OLED_CYBERPUNK(
        displayName = "Cyberpunk Matrix",
        description = "Pitch black with vivid emerald & laser cyan glow",
        isOled = true,
        primaryColorHex = "#00FF88",
        secondaryColorHex = "#00F0FF",
        surfaceColorHex = "#080A0E"
    ),
    OLED_AMETHYST(
        displayName = "Nebula Amethyst",
        description = "Pitch black with Greek Αἰτία luminous purple",
        isOled = true,
        primaryColorHex = "#BC8CFF",
        secondaryColorHex = "#F778BA",
        surfaceColorHex = "#0E0B1A"
    ),
    OLED_MONOKAI(
        displayName = "Tokyo Monokai",
        description = "Pitch black with vibrant multi-color code syntax",
        isOled = true,
        primaryColorHex = "#FF6188",
        secondaryColorHex = "#FC9867",
        surfaceColorHex = "#121317"
    ),
    DARK_SLATE(
        displayName = "GitHub Dark Slate",
        description = "Refined deep charcoal developer workbench",
        isOled = false,
        primaryColorHex = "#58A6FF",
        secondaryColorHex = "#A371F7",
        surfaceColorHex = "#161B22"
    ),
    LIGHT_CLEAN(
        displayName = "Daylight Clean",
        description = "High-contrast clean light mode for bright sun",
        isOled = false,
        primaryColorHex = "#0969DA",
        secondaryColorHex = "#8250DF",
        surfaceColorHex = "#F6F8FA"
    ),
    SYSTEM(
        displayName = "System Default",
        description = "Match your Android OS system settings",
        isOled = false,
        primaryColorHex = "#58A6FF",
        secondaryColorHex = "#A371F7",
        surfaceColorHex = "#161B22"
    )
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.OLED_MIDNIGHT,
    val isAppLockEnabled: Boolean = false,
    val appLockPin: String = "",
    val isBiometricEnabled: Boolean = false,
    val isHapticFeedbackEnabled: Boolean = true,
    val isReducedMotionEnabled: Boolean = false,
    val defaultPriority: Priority = Priority.MEDIUM,
    val defaultProjectId: Long? = null,
    val activeTestingSessionId: Long? = null,
    val hasCompletedOnboarding: Boolean = false,
    val quickCaptureDraft: String = ""
)
