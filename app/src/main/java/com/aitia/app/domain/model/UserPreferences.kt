package com.aitia.app.domain.model

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    OLED
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
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
