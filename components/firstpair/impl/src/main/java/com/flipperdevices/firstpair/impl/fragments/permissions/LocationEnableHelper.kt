package com.flipperdevices.firstpair.impl.fragments.permissions

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.firstpair.impl.R

class LocationEnableHelper(
    private val context: Context,
    private val listener: Listener
) : LogTagProvider {
    private var dialog: AlertDialog? = null
    override val TAG = "LocationEnableHelper"

    fun requestLocationEnabled() {
        if (isLocationEnabled()) {
            warn { "Request location enable, but location already enabled" }
            // Already location enabled
            listener.onLocationEnabled()
            return
        }

        if (dialog != null) {
            return
        }

        dialog = AlertDialog.Builder(context)
            .setTitle(R.string.firstpair_permission_enable_location_title)
            .setMessage(R.string.firstpair_permission_location_dialog)
            .setCancelable(false)
            .setNegativeButton(R.string.firstpair_permission_cancel_btn) { _, _ ->
                verbose { "User click cancel on location enable dialog" }
                listener.onLocationUserDenied()
                dialog?.cancel()
                dialog = null
            }
            .setPositiveButton(R.string.firstpair_permission_settings) { _, _ ->
                verbose { "User click on open setting" }
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog?.cancel()
                dialog = null
            }.create()

        dialog?.show()
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = ContextCompat.getSystemService(
            context, LocationManager::class.java
        )

        if (locationManager == null) {
            warn { "Location manager is null, so return false on isLocationEnabled" }
            return false
        }

        val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)

        if (isLocationEnabled) { // Close dialog if it open
            dialog?.cancel()
            dialog = null
        }

        return isLocationEnabled
    }

    interface Listener {
        fun onLocationEnabled()
        fun onLocationUserDenied()
    }
}
