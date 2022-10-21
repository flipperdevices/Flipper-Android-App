package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import com.flipperdevices.wearable.emulate.impl.model.WearLoadingState
import com.flipperdevices.wearable.emulate.impl.viewmodel.WearEmulateViewModel
import com.google.android.horologist.compose.layout.fillMaxRectangle
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableWearEmulate(
    keyEmulateUiApi: KeyEmulateUiApi,
    onNotFoundNode: () -> Unit,
    onBack: () -> Unit
) {
    val emulateViewModel = tangleViewModel<WearEmulateViewModel>()
    val state by emulateViewModel.getWearEmulateState().collectAsState()
    val modifier = Modifier
        .fillMaxRectangle()
        .padding(horizontal = 10.dp, vertical = 10.dp)

    when (state) {
        WearEmulateState.NotInitialized -> {
            ComposableActionLoading(
                modifier,
                keyEmulateUiApi,
                WearLoadingState.INITIALIZING
            )
            return
        }
        WearEmulateState.ConnectingToFlipper -> {
            ComposableActionLoading(
                modifier,
                keyEmulateUiApi,
                WearLoadingState.CONNECTING_FLIPPER
            )
            return
        }
        is WearEmulateState.EstablishConnection -> {
            ComposableActionLoading(
                modifier,
                keyEmulateUiApi,
                WearLoadingState.CONNECTING_PHONE
            )
            return
        }
        WearEmulateState.NodeFinding -> {
            ComposableActionLoading(
                modifier,
                keyEmulateUiApi,
                WearLoadingState.FINDING_PHONE
            )
            return
        }
        WearEmulateState.TestConnection -> {
            ComposableActionLoading(
                modifier,
                keyEmulateUiApi,
                WearLoadingState.TEST_CONNECTION
            )
            return
        }
        WearEmulateState.UnsupportedFlipper -> {
            ComposableActionDisable(
                modifier,
                keyEmulateUiApi,
                state.keyType
            )
            return
        }
        WearEmulateState.NotFoundNode -> {
            onNotFoundNode()
            return
        }
        is WearEmulateState.Emulating,
        is WearEmulateState.ReadyForEmulate -> {
        } // Ignore
    }

    Box(contentAlignment = Alignment.Center) {
        when (state.keyType) {
            null,
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> ComposableWearSimpleEmulate(
                modifier,
                (state as? WearEmulateState.Emulating)?.progress,
                keyEmulateUiApi,
                emulateViewModel::onClickEmulate,
                emulateViewModel::onStopEmulate
            )
            FlipperKeyType.INFRARED -> onBack()
            FlipperKeyType.SUB_GHZ -> ComposableWearSubGhzEmulate(
                modifier,
                (state as? WearEmulateState.Emulating)?.progress,
                keyEmulateUiApi,
                emulateViewModel::onClickEmulate,
                emulateViewModel::onShortEmulate,
                emulateViewModel::onStopEmulate
            )
        }
    }
}
