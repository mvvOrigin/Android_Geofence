@file:Suppress("PrivatePropertyName")

package com.geomessages

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.extensions.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class LocationService : Service() {

    private val pendingIntent: PendingIntent by lazy {
        LocationReceiver.intent(this)
    }

    companion object {
        fun start(context: Context) =
            ContextCompat.startForegroundService(
                context,
                Intent(context, LocationService::class.java)
            )

        fun stop(context: Context) =
            context.stopService(Intent(context, LocationService::class.java))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        makeForeground(CHANNEL_ID_GEO_SERVICE)
        requestLocationUpdates(this, pendingIntent,
            OnSuccessListener { logD("requestLocationUpdates, success") },
            OnFailureListener { logD("requestLocationUpdates, failed") }
        )
    }

    override fun onDestroy() {
        removeLocationUpdates(this, pendingIntent,
            OnSuccessListener { logD("removeLocationUpdates, success") },
            OnFailureListener { logD("removeLocationUpdates, failed") }
        )
        stopForeground(true)
        stopSelf()
        super.onDestroy()
    }
}