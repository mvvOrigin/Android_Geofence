package com.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.concurrent.TimeUnit

fun requestLocationUpdates(
    context: Context,
    pendingIntent: PendingIntent,
    onSuccessListener: OnSuccessListener<in Void>,
    onFailureListener: OnFailureListener
) {
    val locationRequest = LocationRequest().apply {
        interval = TimeUnit.SECONDS.toMillis(30)
        fastestInterval = TimeUnit.SECONDS.toMillis(10)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    LocationServices.getFusedLocationProviderClient(context)
        .requestLocationUpdates(locationRequest, pendingIntent)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun removeLocationUpdates(
    context: Context,
    pendingIntent: PendingIntent,
    onSuccessListener: OnSuccessListener<in Void>,
    onFailureListener: OnFailureListener
) {
    LocationServices.getFusedLocationProviderClient(context)
        .removeLocationUpdates(pendingIntent)
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener(onFailureListener)
}

fun onReceiveLocation(intent: Intent) {
    val result = LocationResult.extractResult(intent)?.lastLocation
        ?.run { "$latitude $longitude" }
        ?: "not available"

    logD("onReceiveLocation, $result")
}