package com.flipperdevices.firstpair.impl.viewmodels.searching

import android.Manifest
import android.content.Context
import androidx.activity.result.ActivityResult
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
import com.flipperdevices.firstpair.impl.model.PermissionState.ALL_GRANTED
import com.flipperdevices.firstpair.impl.model.PermissionState.BLUETOOTH_PERMISSION
import com.flipperdevices.firstpair.impl.model.PermissionState.BLUETOOTH_PERMISSION_GO_TO_SETTINGS
import com.flipperdevices.firstpair.impl.model.PermissionState.LOCATION_PERMISSION
import com.flipperdevices.firstpair.impl.model.PermissionState.LOCATION_PERMISSION_GO_TO_SETTINGS
import com.flipperdevices.firstpair.impl.model.PermissionState.NOT_REQUESTED_YET
import com.flipperdevices.firstpair.impl.model.PermissionState.TURN_ON_BLUETOOTH
import com.flipperdevices.firstpair.impl.model.PermissionState.TURN_ON_LOCATION
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val DENIED_POSSIBLE_COUNT = 3

@Suppress("TooManyFunctions")
class PermissionStateBuilder(
    context: Context
) : LogTagProvider,
    BluetoothEnableHelper.Listener,
    LocationEnableHelper.Listener,
    PermissionEnableHelper.Listener {
    override val TAG = "PermissionStateBuilder"
    private val bluetoothEnableHelper: BluetoothEnableHelper = BluetoothEnableHelper(
        listener = this
    )
    private val locationEnableHelper: LocationEnableHelper = LocationEnableHelper(
        context = context,
        listener = this
    )
    private val permissionEnableHelper: PermissionEnableHelper = PermissionEnableHelper(
        context = context,
        listener = this,
        permissions = PermissionHelper.getRequiredPermissions()
    )

    fun permissionEnableState() = permissionEnableHelper.state()
    fun bluetoothEnableState() = bluetoothEnableHelper.state()
    fun locationEnableState() = locationEnableHelper.locationDialogState()

    fun processPermissionActivityResult(
        permissionsGrantedMap: Map<String, @JvmSuppressWildcards Boolean>,
    ) = permissionEnableHelper.processPermissionActivityResult(permissionsGrantedMap)

    fun processBluetoothActivityResult(
        activityResult: ActivityResult
    ) = bluetoothEnableHelper.processBluetoothActivityResult(activityResult)

    fun processLocationCancel() = locationEnableHelper.processLocationDecline()
    fun processLocationSettings() = locationEnableHelper.processLocationAccept()

    // If a user has refused one permissive more than three times, we offer him to open the settings
    private val permissionDeniedByUserCount = mutableMapOf<String, Int>()

    private val state = MutableStateFlow(NOT_REQUESTED_YET)

    fun invalidateState(): PermissionState {
        val permissionState = getPreparedState()
        state.update { permissionState }
        return permissionState
    }

    fun getState(): StateFlow<PermissionState> = state

    fun executeStateAction(state: PermissionState) {
        when (state) {
            TURN_ON_BLUETOOTH -> bluetoothEnableHelper.requestBluetoothEnable()
            TURN_ON_LOCATION -> locationEnableHelper.requestLocationEnabled()
            BLUETOOTH_PERMISSION, LOCATION_PERMISSION -> permissionEnableHelper.requestPermissions()
            NOT_REQUESTED_YET,
            BLUETOOTH_PERMISSION_GO_TO_SETTINGS,
            LOCATION_PERMISSION_GO_TO_SETTINGS,
            ALL_GRANTED -> {
                // Do nothing
            }
        }
    }

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
            return TURN_ON_BLUETOOTH
        }

        if (!locationEnableHelper.isLocationEnabled()) {
            info { "Location not enabled, request location enable" }
            return TURN_ON_LOCATION
        }

        info { "All permission granted" }
        return ALL_GRANTED
    }

    @Suppress("SpreadOperator")
    private fun getStateForPermission(permissionNotGranted: List<String>): PermissionState? {
        val deniedCount = permissionDeniedByUserCount.getMaxOf(
            *PermissionHelper.getRequiredPermissions(),
            default = 0
        )
        val shouldShowSettings = deniedCount < DENIED_POSSIBLE_COUNT

        for (permission in permissionNotGranted) {
            when (permission) {
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN ->
                    return if (shouldShowSettings) {
                        BLUETOOTH_PERMISSION
                    } else {
                        BLUETOOTH_PERMISSION_GO_TO_SETTINGS
                    }
                Manifest.permission.ACCESS_FINE_LOCATION ->
                    return if (shouldShowSettings) {
                        LOCATION_PERMISSION
                    } else {
                        LOCATION_PERMISSION_GO_TO_SETTINGS
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
        val permissionState = invalidateState()
        executeStateAction(permissionState)
    }

    override fun onBluetoothUserDenied() {
        invalidateState()
    }

    override fun onLocationEnabled() {
        val permissionState = invalidateState()
        executeStateAction(permissionState)
    }

    override fun onLocationUserDenied() {
        invalidateState()
    }

    override fun onPermissionGranted(permissions: Array<String>) {
        val permissionState = invalidateState()
        executeStateAction(permissionState)
    }

    override fun onPermissionUserDenied(permissions: Array<String>) {
        for (permission in permissions) {
            val currentDeniedCount = permissionDeniedByUserCount.getOrPut(permission) { 0 }
            if (currentDeniedCount > DENIED_POSSIBLE_COUNT) {
                continue
            }
            permissionDeniedByUserCount[permission] = currentDeniedCount + 1
        }

        invalidateState()
    }
}
