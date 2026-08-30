package com.aitia.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AitiaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        com.aitia.app.util.AitiaNotificationHelper.createNotificationChannels(this)
    }
}
