@file:Suppress("SpellCheckingInspection")

package com.geomessages

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.lang.IllegalStateException
import java.util.prefs.PreferencesFactory
import kotlin.random.Random

private const val LOCATION_PERMISSION_CODE = 0x44

private const val GEOFENCING_CHANNEL_ID = "CHANNEL_GEOFENCE"
private const val GEOFENCING_TAG = "geofencing"
private const val GEOFENCING_RADIUS_METERS = 100f

private val pois = mapOf(
//
//    "Round Tower" to LatLng(55.681336, 12.575771)
//    ,"Copenhagen Main Library" to LatLng(55.680869, 12.573612)
//    ,"University of Copenhagen Student Centre" to LatLng(55.680000, 12.573233)

//     "(1)Памятник мячу" to LatLng(50.002243, 36.234659)
//    ,"(2)ХАТОБ" to LatLng(49.999200, 36.232449)
//    ,"(3)AVE Plaza" to LatLng(49.994538, 36.232529)
//    ,"(4)Gorcafe 1654" to LatLng(49.989675, 36.233257)
//    ,"(5)Памятник Сагайдачному" to LatLng(49.988945, 36.242476)
//    ,"(6)майдан Героїв Небесної Сотні" to LatLng(49.989231, 36.247083)
//    ,"(7)Сафари" to LatLng(49.989676, 36.250091)
//    ,"(8)новая почта 2" to LatLng(49.990269, 36.254730)
//    ,"(9)стимул" to LatLng(49.990498, 36.261122)
//    ,"(10)Захисників України" to LatLng(49.989703, 36.264812)
//    ,"(11)стоянка" to LatLng(449.989961, 36.266236)

    "Gothersgade 109, 1123 København" to LatLng(55.683863, 12.576117)
    , "Gothersgade 67-55" to LatLng(55.683146, 12.578949)
    , "Внутрений город" to LatLng(55.682697, 12.580550)
    , "Kvindehuset" to LatLng(55.682322, 12.582028)
    , "Hech Holding Aps" to LatLng(55.681736, 12.583804)
    , "Peter Nielsens Mindefond" to LatLng(55.680955, 12.584614)
    , "Lille Kongensgade" to LatLng(55.679640, 12.585011)
    , "Holmens Kanal 7" to LatLng(55.678200, 12.586438)
    , "Knippels Bridge, Дания" to LatLng(55.674724, 12.587130)
    , "Børnehusbroen" to LatLng(55.672522, 12.591174)
    , "Parkering" to LatLng(55.670253, 12.595466)
    , "Christmas Møllers Pl. 3" to LatLng(55.668680, 12.596947)
)

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val broadcastPendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            this, 0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val servicePendingIntent: PendingIntent by lazy {
        PendingIntent.getService(
            this, 0,
            Intent(this, GeofenceIntentService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkLocationPermissionOrRequest {
            onLocationPermissionGrantedForGeofence()
        }
    }

    private fun onLocationPermissionGrantedForGeofence() {
        // Geofencing
        val geofenceClient = LocationServices.getGeofencingClient(this)
        val geofenceList = pois.map {
            Geofence.Builder()
                .setRequestId(it.key)
                .setCircularRegion(it.value.latitude, it.value.longitude, GEOFENCING_RADIUS_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build()
        }
        val geofenceRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()

        geofenceClient.addGeofences(geofenceRequest, broadcastPendingIntent)?.run {
            addOnSuccessListener {
                Log.d(GEOFENCING_TAG, "Geofences added")
                sendNotification(this@MapsActivity, "status", "Geofences added")
            }
            addOnFailureListener {
                Log.e(GEOFENCING_TAG, "Failed to add geofences", it)
                sendNotification(this@MapsActivity, "status", "Failed to add geofences")
            }
        }
    }

    private fun onLocationPermissionGrantedForMap() {
        if (this::mMap.isInitialized.not()) return

        mMap.isMyLocationEnabled = true

        LocationServices.getFusedLocationProviderClient(this).lastLocation
            .addOnSuccessListener { location ->
                val lat = LatLng(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lat, 16f)
                mMap.moveCamera(cameraUpdate)
                mMap.animateCamera(cameraUpdate)
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        pois.forEach {
            googleMap.addMarker(
                MarkerOptions()
                    .position(it.value)
                    .title(it.key)
            )
        }
        checkLocationPermissionOrRequest {
            onLocationPermissionGrantedForMap()
        }
    }

    override fun onRequestPermissionsResult(
        code: Int,
        permissions: Array<out String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(code, permissions, results)
        if (code == LOCATION_PERMISSION_CODE
            && permissions.contains(ACCESS_FINE_LOCATION)
            && results.contains(PERMISSION_GRANTED)
        ) {
            onLocationPermissionGrantedForMap()
            onLocationPermissionGrantedForGeofence()
        }
    }
}

fun Activity.checkLocationPermissionOrRequest(granted: () -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_CODE
        )
    } else {
        granted()
    }
}

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        onReceiveIntent(context, intent)
    }
}

class GeofenceIntentService(name: String) : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        onReceiveIntent(this, intent ?: return)
    }
}

/**
 * Handle Geofence trigger intent
 */
fun onReceiveIntent(context: Context, intent: Intent) {
    Log.d(GEOFENCING_TAG, "onReceived intent")

    val geofencingEvent = GeofencingEvent.fromIntent(intent)
    if (geofencingEvent.hasError()) {
        val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
        Log.e(GEOFENCING_TAG, errorMessage)
        sendNotification(context, "geofence error", errorMessage)
        return
    }

    val type = when (geofencingEvent.geofenceTransition) {
        Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
        Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
        Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
        else -> "ELSE"
    }

    val text = geofencingEvent.triggeringGeofences.joinToString { it.requestId }

    sendNotification(context, type, text)
}

/**
 * Send notification
 */
fun sendNotification(context: Context, title: String, text: String, id: Int = Random.nextInt()) {
    val message = NotificationCompat.Builder(context, GEOFENCING_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_man)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
    NotificationManagerCompat.from(context)
        .notify(id, message)
}

/**
 * Create notification channel
 */
fun Context.createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "channel_name"
        val descriptionText = "getString(R.string."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(GEOFENCING_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        NotificationManagerCompat.from(this)
            .createNotificationChannel(channel)
    }
}