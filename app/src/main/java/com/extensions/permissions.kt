package com.extensions

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.checkSelfPermission

private const val LOCATION_PERMISSION_CODE = 0x44

private const val mPermission = ACCESS_FINE_LOCATION

/**
 * TODO request ACCESS_BACKGROUND_LOCATION for Q-devices
 */
fun Activity.checkLocationPermissionOrRequest(granted: () -> Unit) {
    if (checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
        requestPermissions(this, arrayOf(mPermission), LOCATION_PERMISSION_CODE)
    } else {
        granted()
    }
}

fun onLocationsPermissionsResult(
    code: Int, permissions: Array<out String>, results: IntArray,
    granted: () -> Unit, denied: () -> Unit
) {
    if (code != LOCATION_PERMISSION_CODE || permissions.isEmpty() || results.isEmpty()) {
        return
    }

    val index = permissions.indexOf(mPermission)
    if (index != -1 && results[index] == PERMISSION_GRANTED) {
        granted()
    } else {
        denied()
    }
}