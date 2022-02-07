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
    override val TAG = "LocationEnableHelper"

    fun requestLocationEnabled() {
        if (isLocationEnabled()) {
            warn { "Request location enable, but location already enabled" }
            // Already location enabled
            listener.onLocationEnabled()
            return
        }

        @Suppress("JoinDeclarationAndAssignment")
        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(context)
            .setTitle(R.string.firstpair_permission_enable_location_title)
            .setMessage(R.string.firstpair_permission_enable_location_desc)
            .setCancelable(false)
            .setNegativeButton(R.string.firstpair_permission_cancel_btn) { _, _ ->
                verbose { "User click cancel on location enable dialog" }
                listener.onLocationUserDenied()
                dialog.cancel()
            }
            .setPositiveButton(R.string.firstpair_permission_settings) { _, _ ->
                verbose { "User click on open setting" }
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.cancel()
            }.create()

        dialog.show()
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = ContextCompat.getSystemService(
            context, LocationManager::class.java
        )

        if (locationManager == null) {
            warn { "Location manager is null, so return false on isLocationEnabled" }
            return false
        }

        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    interface Listener {
        fun onLocationEnabled()
        fun onLocationUserDenied()
    }
}
