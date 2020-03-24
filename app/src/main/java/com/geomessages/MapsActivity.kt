@file:Suppress("SpellCheckingInspection")

package com.geomessages

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.extensions.LOCATION_PERMISSION_CODE
import com.extensions.PointOfInterests
import com.extensions.checkLocationPermissionOrRequest
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkLocationPermissionOrRequest {
            onLocationPermissionGrantedForGeoFence()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        PointOfInterests.forEach {
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
            onLocationPermissionGrantedForGeoFence()
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

    private fun onLocationPermissionGrantedForGeoFence() {
        GeoService.start(this)
    }
}