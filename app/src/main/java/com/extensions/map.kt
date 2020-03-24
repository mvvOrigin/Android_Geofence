package com.extensions

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

LocationServices.getFusedLocationProviderClient(this).lastLocation
.addOnSuccessListener { location ->
    val lat = LatLng(location.latitude, location.longitude)
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(lat, 16f)
    mMap.moveCamera(cameraUpdate)
    mMap.animateCamera(cameraUpdate)
}
*/

fun GoogleMap.addMarkers(point_of_interest: Map<String, LatLng>) {
    point_of_interest.forEach { addMarker(it.toMarker) }
}

val Map.Entry<String, LatLng>.toMarker: MarkerOptions
    get() = MarkerOptions().position(value).title(key)