package com.extensions

import android.app.Service
import androidx.core.app.NotificationCompat
import com.geomessages.R

fun Service.makeForeground(channelID: String) {
    val notification = NotificationCompat.Builder(this, channelID)
        .setSmallIcon(R.drawable.ic_man)
        .setContentTitle("Foreground service")
        .setContentText("tracking your journey")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setWhen(System.currentTimeMillis())
        .setOngoing(true)
        .build()
    startForeground(1, notification)
}