package com.aitia.app

import android.app.Application
import com.aitia.app.di.AppContainer
import com.aitia.app.di.AppDataContainer

class AitiaApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        com.aitia.app.util.AitiaNotificationHelper.createNotificationChannels(this)
    }
}
