@file:Suppress("PrivatePropertyName")

package com.geomessages

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.extensions.CHANNEL_ID_GEO_SERVICE
import com.extensions.makeForeground
import com.extensions.setupGeoFences

class GeoService : Service() {

    companion object {
        fun start(context: Context) =
            context.startService(Intent(context, GeoService::class.java))

        fun stop(context: Context) =
            context.stopService(Intent(context, GeoService::class.java))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        makeForeground(CHANNEL_ID_GEO_SERVICE)
        setupGeoFences(this, GeoReceiver.intent(this))
    }

    override fun onDestroy() {
        stopForeground(true)
        stopSelf()
        super.onDestroy()
    }
}