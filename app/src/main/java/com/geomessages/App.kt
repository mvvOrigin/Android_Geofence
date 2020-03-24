package com.geomessages

import android.app.Application
import com.extensions.CHANNEL_ID_GENERAL
import com.extensions.CHANNEL_ID_GEO_SERVICE
import com.extensions.createNotificationChannel

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(applicationContext, CHANNEL_ID_GEO_SERVICE)
        createNotificationChannel(applicationContext, CHANNEL_ID_GENERAL)
    }
}