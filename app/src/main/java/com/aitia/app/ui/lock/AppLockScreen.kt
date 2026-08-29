package com.aitia.app.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitia.app.R
import com.aitia.app.ui.theme.AitiaBlue
import com.aitia.app.ui.theme.StatusBlocked
import com.aitia.app.util.rememberHapticFeedback

@Composable
fun AppLockScreen(
    correctPin: String,
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val haptic = rememberHapticFeedback()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterVertically,
            verticalArrangement = Arrangement.Center
        ) {
            com.aitia.app.ui.components.AitiaLogo(
                size = 72.dp,
                cornerRadius = 18.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aitia Locked",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isError) "Incorrect PIN. Try again." else "Enter your PIN to access developer notes & logs",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) StatusBlocked else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PIN Dots
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val maxLen = if (correctPin.length > 4) correctPin.length else 4
                for (i in 0 until maxLen) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) (if (isError) StatusBlocked else AitiaBlue) else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Keypad
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIO", "0", "DEL")
            )

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                keys.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        row.forEach { key ->
                            KeypadButton(
                                key = key,
                                onClick = {
                                    haptic.lightTap()
                                    when (key) {
                                        "DEL" -> {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                                isError = false
                                            }
                                        }
                                        "BIO" -> {
                                            // Biometric shortcut simulation / trigger
                                            haptic.success()
                                            onUnlocked()
                                        }
                                        else -> {
                                            if (enteredPin.length < 8) {
                                                val newPin = enteredPin + key
                                                enteredPin = newPin
                                                if (newPin == correctPin) {
                                                    haptic.success()
                                                    onUnlocked()
                                                } else if (newPin.length >= correctPin.length) {
                                                    haptic.warning()
                                                    isError = true
                                                    enteredPin = ""
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (key) {
                "DEL" -> {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                "BIO" -> {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometrics",
                        tint = AitiaBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                else -> {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
