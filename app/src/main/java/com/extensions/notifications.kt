package com.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.geomessages.R
import kotlin.random.Random

const val CHANNEL_ID_GEO_TIPS = "geo-tips"
const val CHANNEL_ID_GEO_SERVICE = "geo-service"

fun createNotificationChannel(context: Context, channelID: String) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, channelID, importance)
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}

fun sendNotification(
    context: Context,
    channelID: String,
    title: String = "default title",
    text: String = "default text",
    id: Int = Random.nextInt()
) {
    val message = NotificationCompat.Builder(context, channelID)
        .setSmallIcon(R.drawable.ic_man)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .build()
    NotificationManagerCompat.from(context).notify(id, message)
}