package com.codaholic.mylight.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import com.codaholic.mylight.network.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class Tools {
    fun getFormattedTimeEvent(time: Long?): String? {
        val newFormat = SimpleDateFormat("H:mm")
        return newFormat.format(Date(time!!))
    }
    fun showSnackbar(view: View, status: Status, message: String): Snackbar{
        var message = message;

        when(status) {
            Status.FAILED -> message = "Failed: " + message
            Status.ERROR -> message = "Error: " + message
            Status.ERROR_CONNECTION -> message = "ERROR_CONNECTION: " + message
        }

        val snackbar = Snackbar.make(view, message,
            Snackbar.LENGTH_LONG).setAction("Action", null)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.BLACK)
        return snackbar;
    }

    fun configActivityMaps(googleMap: GoogleMap): GoogleMap? { // set map type
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        // Enable / Disable zooming controls
        googleMap.uiSettings.isZoomControlsEnabled = false
        // Enable / Disable Compass icon
        googleMap.uiSettings.isCompassEnabled = true
        // Enable / Disable Rotate gesture
        googleMap.uiSettings.isRotateGesturesEnabled = true
        // Enable / Disable zooming functionality
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isScrollGesturesEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        return googleMap
    }

    fun showToast(context: Context, message: String?){
        message?.let { Toast.makeText(context, it,Toast.LENGTH_LONG).show() }
    }

    fun changeMenuIconColor(menu: Menu, @ColorInt color: Int) {
        for (i in 0 until menu.size()) {
            val drawable = menu.getItem(i).icon ?: continue
            drawable.mutate()
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun dpToPx(c: Context, dp: Int): Int {
        val r = c.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }
}