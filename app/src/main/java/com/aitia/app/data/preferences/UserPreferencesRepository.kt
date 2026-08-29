package com.aitia.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aitia.app.domain.model.AppThemeMode
import com.aitia.app.domain.model.Priority
import com.aitia.app.domain.model.UserPreferences
import com.aitia.app.util.SecurityUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aitia_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LOCK_PIN = stringPreferencesKey("app_lock_pin")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val DEFAULT_PRIORITY = stringPreferencesKey("default_priority")
        val DEFAULT_PROJECT_ID = longPreferencesKey("default_project_id")
        val ACTIVE_SESSION_ID = longPreferencesKey("active_session_id")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val QUICK_CAPTURE_DRAFT = stringPreferencesKey("quick_capture_draft")
        val SHAKE_TO_REPORT = booleanPreferencesKey("shake_to_report")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeStr = preferences[PreferenceKeys.THEME_MODE] ?: AppThemeMode.OLED_MIDNIGHT.name
        val themeMode = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.OLED_MIDNIGHT)

        val defaultPriorityStr = preferences[PreferenceKeys.DEFAULT_PRIORITY] ?: Priority.MEDIUM.name
        val defaultPriority = runCatching { Priority.valueOf(defaultPriorityStr) }.getOrDefault(Priority.MEDIUM)

        UserPreferences(
            themeMode = themeMode,
            isAppLockEnabled = preferences[PreferenceKeys.APP_LOCK_ENABLED] ?: false,
            appLockPin = preferences[PreferenceKeys.APP_LOCK_PIN] ?: "",
            isBiometricEnabled = preferences[PreferenceKeys.BIOMETRIC_ENABLED] ?: false,
            isHapticFeedbackEnabled = preferences[PreferenceKeys.HAPTIC_ENABLED] ?: true,
            isReducedMotionEnabled = preferences[PreferenceKeys.REDUCED_MOTION] ?: false,
            defaultPriority = defaultPriority,
            defaultProjectId = preferences[PreferenceKeys.DEFAULT_PROJECT_ID],
            activeTestingSessionId = preferences[PreferenceKeys.ACTIVE_SESSION_ID],
            hasCompletedOnboarding = preferences[PreferenceKeys.ONBOARDING_COMPLETED] ?: false,
            quickCaptureDraft = preferences[PreferenceKeys.QUICK_CAPTURE_DRAFT] ?: "",
            isShakeToReportEnabled = preferences[PreferenceKeys.SHAKE_TO_REPORT] ?: false
        )
    }

    suspend fun setThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setAppLock(enabled: Boolean, pin: String = "", biometric: Boolean = false) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.APP_LOCK_ENABLED] = enabled
            if (pin.isNotEmpty()) {
                preferences[PreferenceKeys.APP_LOCK_PIN] = SecurityUtil.hashPin(pin)
            }
            preferences[PreferenceKeys.BIOMETRIC_ENABLED] = biometric
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun setReducedMotion(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.REDUCED_MOTION] = enabled
        }
    }

    suspend fun setDefaultPriority(priority: Priority) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DEFAULT_PRIORITY] = priority.name
        }
    }

    suspend fun setDefaultProjectId(projectId: Long?) {
        context.dataStore.edit { preferences ->
            if (projectId != null) {
                preferences[PreferenceKeys.DEFAULT_PROJECT_ID] = projectId
            } else {
                preferences.remove(PreferenceKeys.DEFAULT_PROJECT_ID)
            }
        }
    }

    suspend fun setActiveTestingSessionId(sessionId: Long?) {
        context.dataStore.edit { preferences ->
            if (sessionId != null) {
                preferences[PreferenceKeys.ACTIVE_SESSION_ID] = sessionId
            } else {
                preferences.remove(PreferenceKeys.ACTIVE_SESSION_ID)
            }
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setQuickCaptureDraft(draft: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.QUICK_CAPTURE_DRAFT] = draft
        }
    }

    suspend fun setShakeToReport(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SHAKE_TO_REPORT] = enabled
        }
    }
}
