@file:Suppress("SpellCheckingInspection")

package com.geomessages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.extensions.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        /**
         * map disabled due to experiment with location tracking out of map, use:
         * - foreground service
         * - other apps e.g. Google Maps
         */
        // mapFragment.getMapAsync(this)

        checkLocationPermissionOrRequest {
            onLocationPermissionGrantedForGeoFence()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.addMarkers(PointOfInterests)

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
        onLocationsPermissionsResult(code, permissions, results,
            granted = {
                onLocationPermissionGrantedForMap()
                onLocationPermissionGrantedForGeoFence()
            },
            denied = {
                logD("permission denied, $permissions, $results")
            }
        )
    }

    private fun onLocationPermissionGrantedForMap() {
        if (this::mMap.isInitialized.not())
        mMap.isMyLocationEnabled = true
    }

    private fun onLocationPermissionGrantedForGeoFence() {
        /**
         * comment next line to disable foreground service &
         * test other apps who tracking location
         */
        LocationService.start(this)
        setupGeoFences(this, GeoFenceReceiver.intent(this))
    }
}