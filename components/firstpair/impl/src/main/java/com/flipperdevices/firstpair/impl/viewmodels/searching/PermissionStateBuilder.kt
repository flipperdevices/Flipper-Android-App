package com.flipperdevices.firstpair.impl.viewmodels.searching

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.core.ktx.jre.getMaxOf
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.firstpair.impl.fragments.permissions.BluetoothEnableHelper
import com.flipperdevices.firstpair.impl.fragments.permissions.LocationEnableHelper
import com.flipperdevices.firstpair.impl.fragments.permissions.PermissionEnableHelper
import com.flipperdevices.firstpair.impl.model.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val DENIED_POSSIBLE_COUNT = 3

class PermissionStateBuilder(
    fragment: Fragment,
    context: Context
) : LogTagProvider,
    BluetoothEnableHelper.Listener,
    LocationEnableHelper.Listener,
    PermissionEnableHelper.Listener {
    override val TAG = "PermissionStateBuilder"
    private var bluetoothEnableHelper: BluetoothEnableHelper = BluetoothEnableHelper(
        fragment = fragment,
        listener = this
    )
    private var locationEnableHelper: LocationEnableHelper = LocationEnableHelper(
        context = context,
        listener = this
    )
    private var permissionEnableHelper: PermissionEnableHelper = PermissionEnableHelper(
        fragment = fragment,
        context = context,
        listener = this,
        permissions = PermissionHelper.getRequiredPermissions()
    )

    // If a user has refused one permissive more than three times, we offer him to open the settings
    private var permissionDeniedByUserCount = mutableMapOf<String, Int>()

    private val state = MutableStateFlow<PermissionState>(PermissionState.NOT_REQUESTED_YET)

    fun invalidate() {
        state.update { getPreparedState() }
    }

    fun getState(): StateFlow<PermissionState> = state

    /**
     * @return state before searching. Return null if all preparing state is finished
     */
    private fun getPreparedState(): PermissionState {
        val permissionNotGranted = permissionEnableHelper.getUngrantedPermission()
        if (permissionNotGranted.isNotEmpty()) {
            info { "Permission $permissionNotGranted not granted, request it" }
            val permissionState = getStateForPermission(permissionNotGranted)
            if (permissionState != null) {
                return permissionState
            } else if (BuildConfig.INTERNAL) {
                error("Can't find state for $permissionNotGranted")
            }
        }

        if (!bluetoothEnableHelper.isBluetoothEnabled()) {
            info { "Bluetooth not enabled, request bluetooth enable" }
            bluetoothEnableHelper.requestBluetoothEnable()
            return PermissionState.TURN_ON_BLUETOOTH
        }

        if (!locationEnableHelper.isLocationEnabled()) {
            info { "Location not enabled, request location enable" }
            locationEnableHelper.requestLocationEnabled()
            return PermissionState.TURN_ON_LOCATION
        }

        info { "All permission granted" }
        return PermissionState.ALL_GRANTED
    }

    @Suppress("SpreadOperator")
    private fun getStateForPermission(permissionNotGranted: List<String>): PermissionState? {
        val deniedCount = permissionDeniedByUserCount.getMaxOf(
            *PermissionHelper.getRequiredPermissions(),
            default = 0
        )
        val shouldShowSettings = deniedCount < DENIED_POSSIBLE_COUNT

        if (!shouldShowSettings) {
            permissionEnableHelper.requestPermissions()
        }

        for (permission in permissionNotGranted) {
            when (permission) {
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN -> {
                    return if (shouldShowSettings) {
                        PermissionState.BLUETOOTH_PERMISSION_GO_TO_SETTINGS
                    } else PermissionState.BLUETOOTH_PERMISSION
                }
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    return if (shouldShowSettings) {
                        PermissionState.LOCATION_PERMISSION_GO_TO_SETTINGS
                    } else PermissionState.LOCATION_PERMISSION
                }
                else -> {
                    error { "Unknown permission $permission, skip" }
                    continue
                }
            }
        }
        return null
    }

    override fun onBluetoothEnabled() {
        invalidate()
    }

    override fun onBluetoothUserDenied() {
        invalidate()
    }

    override fun onLocationEnabled() {
        invalidate()
    }

    override fun onLocationUserDenied() {
        invalidate()
    }

    override fun onPermissionGranted(permissions: Array<String>) {
        invalidate()
    }

    override fun onPermissionUserDenied(permissions: Array<String>) {
        for (permission in permissions) {
            val currentDeniedCount = permissionDeniedByUserCount.getOrPut(permission) { 0 }
            if (currentDeniedCount > DENIED_POSSIBLE_COUNT) {
                continue
            }
            permissionDeniedByUserCount[permission] = currentDeniedCount + 1
        }

        invalidate()
    }
}
