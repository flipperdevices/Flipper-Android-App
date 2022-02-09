package com.flipperdevices.firstpair.impl.viewmodels

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.firstpair.impl.model.DevicePairState
import com.flipperdevices.firstpair.impl.model.ScanState
import com.flipperdevices.firstpair.impl.model.SearchingContent
import com.flipperdevices.firstpair.impl.model.SearchingState
import com.flipperdevices.firstpair.impl.viewmodels.connecting.PairDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import com.flipperdevices.firstpair.impl.viewmodels.searching.PermissionStateBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

class SearchStateBuilder(
    private val permissionStateBuilder: PermissionStateBuilder,
    private val viewModelSearch: BLEDeviceViewModel,
    viewModelConnecting: PairDeviceViewModel,
    scope: CoroutineScope
) : LogTagProvider {
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

    fun invalidate() {
        if (freezeInvalidate) {
            return
        }
        permissionStateBuilder.invalidate()
        freezeInvalidate()
    }

    fun freezeInvalidate() {
        freezeInvalidate = true
    }

    fun unfreezeInvalidate() {
        freezeInvalidate = false
    }

    fun getState(): StateFlow<SearchingState> = state

    private suspend fun combineState(
        permissionState: SearchingContent.PermissionRequest?,
        scanState: ScanState,
        pairState: DevicePairState
    ) {
        if (pairState is DevicePairState.Connected) {
            state.emit(SearchingState(content = SearchingContent.Finished(pairState.address)))
            return
        }

        if (permissionState != null) {
            state.emit(SearchingState(content = permissionState))
            return
        }
        when (scanState) {
            ScanState.Searching -> state.emit(
                SearchingState(
                    showSearching = true, showHelp = true,
                    content = SearchingContent.Searching
                )
            )
            ScanState.Timeout -> state.emit(
                SearchingState(
                    showSearching = false, showHelp = true,
                    content = SearchingContent.FlipperNotFound
                )
            )
            is ScanState.Stopped -> viewModelSearch.startScanIfNotYet()
            is ScanState.Founded -> state.emit(
                SearchingState(
                    showSearching = true, showHelp = true,
                    content = SearchingContent.FoundedDevices(
                        devices = scanState.devices,
                        selectedAddress = when (pairState) {
                            is DevicePairState.Connected,
                            DevicePairState.NotInitialized -> null
                            is DevicePairState.Connecting -> pairState.address
                        }
                    )
                )
            )
        }
    }
}
