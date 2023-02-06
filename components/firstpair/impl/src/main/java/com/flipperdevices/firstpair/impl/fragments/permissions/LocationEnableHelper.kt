package com.flipperdevices.firstpair.impl.fragments.permissions

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocationEnableHelper(
    private val context: Context,
    private val listener: Listener
) : LogTagProvider {
    private val _state = MutableStateFlow(false)
    fun locationDialogState() = _state.asStateFlow()

    override val TAG = "LocationEnableHelper"

    fun processLocationDecline() {
        verbose { "User click cancel on location enable dialog" }
        listener.onLocationUserDenied()
        _state.update { false }
    }

    fun processLocationAccept() {
        verbose { "User click on open setting" }
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        _state.update { false }
    }

    fun requestLocationEnabled() {
        if (isLocationEnabled()) {
            warn { "Request location enable, but location already enabled" }
            // Already location enabled
            listener.onLocationEnabled()
            return
        }

        _state.update { true }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = ContextCompat.getSystemService(
            context,
            LocationManager::class.java
        )

        if (locationManager == null) {
            warn { "Location manager is null, so return false on isLocationEnabled" }
            return false
        }

        val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)

        if (isLocationEnabled) { // Close dialog if it open
            _state.update { false }
        }

        return isLocationEnabled
    }

    interface Listener {
        fun onLocationEnabled()
        fun onLocationUserDenied()
    }
}
