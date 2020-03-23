package com.geomessages

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.lang.IllegalStateException

fun Context.dpToPx(dp: Int): Int = dp.times(resources.displayMetrics.density).toInt()

//        googleMap.setOnMapLoadedCallback {
//            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
//                LatLngBounds.Builder().apply { pois.values.forEach { include(it) } }.build(),
//                dpToPx(24)
//            )
//            googleMap.moveCamera(cameraUpdate)
//            googleMap.animateCamera(cameraUpdate)
//        }

//        .icon(bitmapDescriptorFromVector(R.drawable.ic_man))

fun Context.bitmapDescriptorFromVector(@DrawableRes res: Int): BitmapDescriptor {
    val vector = ContextCompat.getDrawable(this, res) ?: throw IllegalStateException()
    val width = vector.intrinsicWidth
    val height = vector.intrinsicHeight
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    vector.setBounds(0, 0, width, height)
    vector.draw(Canvas(bitmap))
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun Context.logOffline(message: String) {
    val key = "alerts"
    val prefs = getSharedPreferences("geo-logs", MODE_PRIVATE)
    val current = prefs.getString(key, "") ?: ""
    val new = StringBuilder(current).append("$message\n").toString()
    prefs.edit().putString(key, new).apply()
}