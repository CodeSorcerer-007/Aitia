package com.aitia.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aitia.app.domain.model.AppThemeMode
import com.aitia.app.ui.lock.AppLockScreen
import com.aitia.app.ui.navigation.AitiaNavHost
import com.aitia.app.ui.theme.AitiaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val app = application as AitiaApplication
        val appContainer = app.container

        val isQuickCaptureIntent = intent?.data?.host == "quickcapture"

        setContent {
            val scope = rememberCoroutineScope()
            val preferences by appContainer.preferencesRepository.userPreferences.collectAsState(
                initial = com.aitia.app.domain.model.UserPreferences()
            )

            var isUnlocked by remember { mutableStateOf(!preferences.isAppLockEnabled) }

            // Re-lock if preference changes
            LaunchedEffectKey(preferences.isAppLockEnabled) {
                if (!preferences.isAppLockEnabled) {
                    isUnlocked = true
                }
            }

            AitiaTheme(themeMode = preferences.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (preferences.isAppLockEnabled && !isUnlocked) {
                        AppLockScreen(
                            correctPin = preferences.appLockPin.ifEmpty { "1234" },
                            onUnlocked = { isUnlocked = true }
                        )
                    } else {
                        AitiaNavHost(
                            appContainer = appContainer,
                            hasCompletedOnboarding = preferences.hasCompletedOnboarding,
                            onCompleteOnboarding = {
                                scope.launch {
                                    appContainer.preferencesRepository.setOnboardingCompleted(true)
                                }
                            },
                            initialTriggerQuickCapture = isQuickCaptureIntent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LaunchedEffectKey(key: Any?, block: suspend () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(key) {
        block()
    }
}
