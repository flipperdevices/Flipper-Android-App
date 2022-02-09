package com.flipperdevices.firstpair.impl.viewmodels.searching

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
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
import com.flipperdevices.firstpair.impl.model.SearchingContent
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
    override val TAG = "PermissionHelper"
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

    private val state = MutableStateFlow<SearchingContent.PermissionRequest?>(
        SearchingContent.TurnOnBluetooth
    )

    fun invalidate() {
        state.update { getPreparedState() }
    }

    fun getState(): StateFlow<SearchingContent.PermissionRequest?> = state

    /**
     * @return state before searching. Return null if all preparing state is finished
     */
    private fun getPreparedState(): SearchingContent.PermissionRequest? {
        if (!bluetoothEnableHelper.isBluetoothEnabled()) {
            info { "Bluetooth not enabled, request bluetooth enable" }
            bluetoothEnableHelper.requestBluetoothEnable()
            return SearchingContent.TurnOnBluetooth
        }

        if (!locationEnableHelper.isLocationEnabled()) {
            info { "Location not enabled, request location enable" }
            locationEnableHelper.requestLocationEnabled()
            return SearchingContent.TurnOnLocation
        }

        val permissionNotGranted = permissionEnableHelper.getUngrantedPermission()
        if (permissionNotGranted.isNotEmpty()) {
            info { "Permission $permissionNotGranted not granted, request it" }
            permissionEnableHelper.requestPermissions()
            val permissionState = getStateForPermission(permissionNotGranted)
            if (permissionState != null) {
                return permissionState
            } else if (BuildConfig.INTERNAL) {
                error("Can't find state for $permissionNotGranted")
            }
        }

        info { "All permission granted" }
        return null
    }

    @Suppress("LoopWithTooManyJumpStatements")
    private fun getStateForPermission(permissionNotGranted: List<String>): SearchingContent.PermissionRequest? {
        for (permission in permissionNotGranted) {
            when (permission) {
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        continue
                    }
                    @SuppressLint("InlinedApi")
                    val deniedCount = permissionDeniedByUserCount.getMaxOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                        default = 0
                    )
                    return SearchingContent.BluetoothPermission(
                        deniedCount < DENIED_POSSIBLE_COUNT
                    )
                }
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    val deniedCount = permissionDeniedByUserCount.getMaxOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        default = 0
                    )
                    return SearchingContent.LocationPermission(
                        deniedCount < DENIED_POSSIBLE_COUNT
                    )
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
