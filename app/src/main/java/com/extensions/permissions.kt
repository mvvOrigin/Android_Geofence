package com.extensions

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.checkSelfPermission

const val LOCATION_PERMISSION_CODE = 0x44

fun Activity.checkLocationPermissionOrRequest(granted: () -> Unit) {
    if (checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
        requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
    } else {
        granted()
    }
}