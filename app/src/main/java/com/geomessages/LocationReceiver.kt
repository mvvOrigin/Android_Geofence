package com.geomessages

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.extensions.onReceiveLocation

class LocationReceiver : BroadcastReceiver() {

    companion object {
        fun intent(context: Context): PendingIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, LocationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        onReceiveLocation(intent)
    }
}