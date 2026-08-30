package com.aitia.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aitia.app.domain.model.AppThemeMode
import com.aitia.app.ui.lock.AppLockScreen
import com.aitia.app.ui.navigation.AitiaNavHost
import com.aitia.app.ui.theme.AitiaTheme
import android.annotation.SuppressLint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.aitia.app.data.preferences.UserPreferencesRepository

@SuppressLint("InvalidFragmentVersionForActivityResult")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    private val _quickCaptureTrigger = MutableStateFlow(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data?.host == "quickcapture") {
            _quickCaptureTrigger.value = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (intent?.data?.host == "quickcapture") {
            _quickCaptureTrigger.value = true
        }

        setContent {
            val scope = rememberCoroutineScope()
            val preferences by preferencesRepository.userPreferences.collectAsState(
                initial = com.aitia.app.domain.model.UserPreferences()
            )

            var isUnlocked by remember { mutableStateOf(!preferences.isAppLockEnabled) }

            // Re-lock if preference changes
            LaunchedEffectKey(preferences.isAppLockEnabled) {
                if (!preferences.isAppLockEnabled) {
                    isUnlocked = true
                }
            }

            // Lock app on background
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner, preferences.isAppLockEnabled) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_STOP && preferences.isAppLockEnabled) {
                        isUnlocked = false
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // Shake to Report Sensor Listener
            DisposableEffect(preferences.isShakeToReportEnabled) {
                if (preferences.isShakeToReportEnabled) {
                    val detector = com.aitia.app.util.ShakeDetector(this@MainActivity) {
                        _quickCaptureTrigger.value = true
                    }
                    detector.start()
                    onDispose {
                        detector.stop()
                    }
                } else {
                    onDispose {}
                }
            }

            val triggerQuickCapture by _quickCaptureTrigger.collectAsState()
            
            // Consume trigger
            LaunchedEffect(triggerQuickCapture) {
                if (triggerQuickCapture) {
                    _quickCaptureTrigger.value = false
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
                            hasCompletedOnboarding = preferences.hasCompletedOnboarding,
                            onCompleteOnboarding = {
                                scope.launch {
                                    preferencesRepository.setOnboardingCompleted(true)
                                }
                            },
                            initialTriggerQuickCapture = triggerQuickCapture
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
