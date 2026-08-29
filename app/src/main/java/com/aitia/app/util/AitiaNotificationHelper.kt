package com.aitia.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.aitia.app.MainActivity
import com.aitia.app.R

object AitiaNotificationHelper {

    private const val CHANNEL_ID_CRITICAL = "aitia_critical_channel"
    private const val CHANNEL_NAME_CRITICAL = "Critical Defect Alerts"
    private const val CHANNEL_ID_SESSION = "aitia_session_channel"
    private const val CHANNEL_NAME_SESSION = "Testing Session Reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val criticalChannel = NotificationChannel(
                CHANNEL_ID_CRITICAL,
                CHANNEL_NAME_CRITICAL,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for unresolved high/critical defects."
                enableVibration(true)
            }

            val sessionChannel = NotificationChannel(
                CHANNEL_ID_SESSION,
                CHANNEL_NAME_SESSION,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing reminders for active testing sessions."
            }

            notificationManager.createNotificationChannel(criticalChannel)
            notificationManager.createNotificationChannel(sessionChannel)
        }
    }

    fun showCriticalIssueAlert(context: Context, issueId: Long, title: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            issueId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CRITICAL)
            .setSmallIcon(R.drawable.ic_aitia_logo)
            .setContentTitle("Critical Issue Alert")
            .setContentText("#$issueId: $title")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(issueId.toInt(), notification)
    }

    fun showSessionActiveNotification(context: Context, sessionName: String, duration: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SESSION)
            .setSmallIcon(R.drawable.ic_aitia_logo)
            .setContentTitle("Testing Session Active: $sessionName")
            .setContentText("Recording observations · Duration: $duration")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(9999, notification)
    }

    fun cancelSessionNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(9999)
    }
}
