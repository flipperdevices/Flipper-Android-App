package com.flipperdevices.firstpair.impl.viewmodels

import android.content.Context
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.firstpair.impl.model.DevicePairState
import com.flipperdevices.firstpair.impl.model.PermissionState
import com.flipperdevices.firstpair.impl.model.PermissionState.ALL_GRANTED
import com.flipperdevices.firstpair.impl.model.PermissionState.BLUETOOTH_PERMISSION
import com.flipperdevices.firstpair.impl.model.PermissionState.BLUETOOTH_PERMISSION_GO_TO_SETTINGS
import com.flipperdevices.firstpair.impl.model.PermissionState.LOCATION_PERMISSION
import com.flipperdevices.firstpair.impl.model.PermissionState.LOCATION_PERMISSION_GO_TO_SETTINGS
import com.flipperdevices.firstpair.impl.model.PermissionState.NOT_REQUESTED_YET
import com.flipperdevices.firstpair.impl.model.PermissionState.TURN_ON_BLUETOOTH
import com.flipperdevices.firstpair.impl.model.PermissionState.TURN_ON_LOCATION
import com.flipperdevices.firstpair.impl.model.ScanState
import com.flipperdevices.firstpair.impl.model.SearchingContent
import com.flipperdevices.firstpair.impl.model.SearchingState
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionStateBuilder
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

class SearchStateBuilder(
    private val context: Context,
    private val permissionStateBuilder: PermissionStateBuilder,
    private val viewModelSearch: BLEDeviceViewModel,
    viewModelConnecting: PairDeviceViewModel,
    scope: CoroutineScope
) : LogTagProvider, Lifecycle.Callbacks {
    override val TAG = "SearchStateBuilder"

    private val state = MutableStateFlow(
        SearchingState(content = SearchingContent.Searching)
    )

    init {
        combine(
            permissionStateBuilder.getState(),
            viewModelSearch.getState(),
            viewModelConnecting.getConnectionState()
        ) { permissionState, scanState, pairState ->
            combineState(permissionState, scanState, pairState)
        }.launchIn(scope)
    }

    // If freeze is true, we don't change state on invalidate
    private var freezeInvalidate = false

    override fun onResume() = invalidate()

    fun invalidate() {
        val permissionState = permissionStateBuilder.invalidateState()
        if (!freezeInvalidate) {
            permissionStateBuilder.executeStateAction(permissionState)
        }
        freezeInvalidate()
    }

    fun freezeInvalidate() {
        freezeInvalidate = true
    }

    fun unfreezeInvalidate() {
        freezeInvalidate = false
    }

    fun resetByUser() {
        unfreezeInvalidate()
        viewModelSearch.stopScan()
        viewModelSearch.startScanIfNotYet()
    }

    fun getState(): StateFlow<SearchingState> = state

    private suspend fun combineState(
        permissionState: PermissionState,
        scanState: ScanState,
        pairState: DevicePairState
    ) {
        info {
            "Received permissionState: $permissionState, " +
                "scanState: $scanState, " +
                "pairState: $pairState"
        }
        if (pairState is DevicePairState.Connected) {
            state.emit(
                SearchingState(
                    content = SearchingContent.Finished(
                        pairState.address,
                        pairState.deviceName
                    )
                )
            )
            return
        }

        if (permissionState != ALL_GRANTED) {
            applyPermissionState(permissionState)
            return
        }
        applyScanState(scanState, pairState)
    }

    private suspend fun applyPermissionState(permissionState: PermissionState) {
        viewModelSearch.stopScan()
        val permissionContent = when (permissionState) {
            TURN_ON_BLUETOOTH, NOT_REQUESTED_YET -> SearchingContent.TurnOnBluetooth(
                searchStateHolder = this
            )

            TURN_ON_LOCATION -> SearchingContent.TurnOnLocation(
                searchStateHolder = this,
                context = context
            )

            BLUETOOTH_PERMISSION ->
                SearchingContent.BluetoothPermission(
                    searchStateHolder = this,
                    context = context,
                    requestedFirstTime = true
                )

            BLUETOOTH_PERMISSION_GO_TO_SETTINGS ->
                SearchingContent.BluetoothPermission(
                    searchStateHolder = this,
                    context = context,
                    requestedFirstTime = false
                )

            LOCATION_PERMISSION ->
                SearchingContent.LocationPermission(
                    searchStateHolder = this,
                    context = context,
                    requestedFirstTime = true
                )

            LOCATION_PERMISSION_GO_TO_SETTINGS ->
                SearchingContent.LocationPermission(
                    searchStateHolder = this,
                    context = context,
                    requestedFirstTime = false
                )

            ALL_GRANTED -> null
        }
        if (permissionContent != null) {
            state.emit(SearchingState(content = permissionContent))
        }
    }

    private suspend fun applyScanState(scanState: ScanState, pairState: DevicePairState) {
        unfreezeInvalidate()
        when (scanState) {
            ScanState.Searching -> state.emit(
                SearchingState(
                    showSearching = true,
                    showHelp = true,
                    content = SearchingContent.Searching
                )
            )

            ScanState.Timeout -> state.emit(
                SearchingState(
                    showSearching = false,
                    showHelp = true,
                    content = SearchingContent.FlipperNotFound(this)
                )
            )

            is ScanState.Stopped -> viewModelSearch.startScanIfNotYet()
            is ScanState.Founded -> state.emit(
                SearchingState(
                    showSearching = true,
                    showHelp = true,
                    content = SearchingContent.FoundedDevices(
                        devices = scanState.devices.toPersistentList(),
                        pairState = pairState
                    )
                )
            )
        }
    }
}
