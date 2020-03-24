package com.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.*

const val GEO_RADIUS_METERS = 100f

fun setupGeoFences(context: Context, intent: PendingIntent) {
    val geoClient = LocationServices.getGeofencingClient(context)

    val geoList = PointOfInterests.map {
        Geofence.Builder()
            .setRequestId(it.key)
            .setCircularRegion(it.value.latitude, it.value.longitude, GEO_RADIUS_METERS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()
    }

    val geoRequest = GeofencingRequest.Builder().apply {
        setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        addGeofences(geoList)
    }.build()

    geoClient.addGeofences(geoRequest, intent).run {
        addOnSuccessListener {
            logD("addGeoFences, success")
        }
        addOnFailureListener {
            logD("addGeoFences, failed")
        }
    }
}

fun onReceiveGeoFence(context: Context, intent: Intent) {

    val geoEvent = GeofencingEvent.fromIntent(intent)
    if (geoEvent.hasError()) {
        val errorMessage = GeofenceStatusCodes.getStatusCodeString(geoEvent.errorCode)
        sendNotification(context, CHANNEL_ID_GENERAL,"invalid geo-fence", errorMessage)
        logI("onReceiveGeoFence, $errorMessage")
        return
    }

    val type = when (geoEvent.geofenceTransition) {
        Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
        Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
        Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
        else -> "ELSE"
    }

    val text = geoEvent.triggeringGeofences.joinToString { it.requestId }

    sendNotification(context, CHANNEL_ID_GENERAL, type, text)
    logI("onReceiveGeoFence, $type, $text")
}