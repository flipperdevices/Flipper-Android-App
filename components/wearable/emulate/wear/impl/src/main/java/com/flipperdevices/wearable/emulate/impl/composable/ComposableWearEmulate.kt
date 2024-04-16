package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.impl.helper.WearEmulateState
import com.flipperdevices.wearable.emulate.impl.viewmodel.WearEmulateViewModel
import com.flipperdevices.wearable.emulate.impl.viewmodel.WearLoadingState

@Composable
@Suppress("LongMethod")
fun ComposableWearEmulate(
    keyEmulateUiApi: KeyEmulateUiApi,
    onBack: () -> Unit,
    emulateViewModel: WearEmulateViewModel
) {
    val state by emulateViewModel.getWearEmulateState().collectAsState()
    val modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 10.dp)
    when (state) {
        WearEmulateState.NotInitialized ->
            ComposableActionLoading(
                keyEmulateUiApi,
                WearLoadingState.INITIALIZING,
                modifier
            )
        WearEmulateState.ConnectingToFlipper ->
            ComposableActionLoading(
                keyEmulateUiApi,
                WearLoadingState.CONNECTING_FLIPPER,
                modifier
            )
        WearEmulateState.NodeFinding ->
            ComposableActionLoading(
                keyEmulateUiApi,
                WearLoadingState.FINDING_PHONE,
                modifier
            )
        WearEmulateState.TestConnection ->
            ComposableActionLoading(
                keyEmulateUiApi,
                WearLoadingState.TEST_CONNECTION,
                modifier
            )
        WearEmulateState.UnsupportedFlipper ->
            ComposableActionDisable(
                keyEmulateUiApi,
                null,
                modifier
            )
        WearEmulateState.NotFoundNode ->
            ComposableActionLoading(
                keyEmulateUiApi,
                WearLoadingState.NOT_FOUND_PHONE,
                modifier
            )
        is WearEmulateState.Emulating,
        is WearEmulateState.ReadyForEmulate ->
            EmulateButton(state, keyEmulateUiApi, emulateViewModel, modifier, onBack)
    }
}

@Composable
private fun EmulateButton(
    state: WearEmulateState,
    keyEmulateUiApi: KeyEmulateUiApi,
    emulateViewModel: WearEmulateViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state.keyType) {
            null,
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> ComposableWearSimpleEmulate(
                (state as? WearEmulateState.Emulating)?.progress,
                keyEmulateUiApi,
                emulateViewModel::onClickEmulate,
                onStopEmulate = emulateViewModel::onStopEmulate
            )
            FlipperKeyType.INFRARED -> onBack()
            FlipperKeyType.SUB_GHZ -> ComposableWearSubGhzEmulate(
                (state as? WearEmulateState.Emulating)?.progress,
                keyEmulateUiApi,
                emulateViewModel::onClickEmulate,
                emulateViewModel::onShortEmulate,
                onStopEmulate = emulateViewModel::onStopEmulate
            )
        }
    }
}
