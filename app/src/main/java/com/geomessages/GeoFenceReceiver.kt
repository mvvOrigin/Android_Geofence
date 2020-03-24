package com.geomessages

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.extensions.onReceiveGeoFence

class GeoFenceReceiver : BroadcastReceiver() {

    companion object {
        fun intent(context: Context): PendingIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, GeoFenceReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        onReceiveGeoFence(context, intent)
    }
}



