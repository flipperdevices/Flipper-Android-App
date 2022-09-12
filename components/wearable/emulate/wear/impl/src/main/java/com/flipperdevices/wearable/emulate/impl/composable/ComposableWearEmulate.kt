package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import com.flipperdevices.wearable.emulate.impl.viewmodel.WearEmulateViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableWearEmulate(
    keyEmulateUiApi: KeyEmulateUiApi,
    onNotFoundNode: () -> Unit,
    onBack: () -> Unit
) {
    val emulateViewModel = tangleViewModel<WearEmulateViewModel>()
    val state by emulateViewModel.getWearEmulateState().collectAsState()
    if (state is WearEmulateState.NotFoundNode) {
        onNotFoundNode()
        return
    }
    Box(contentAlignment = Alignment.Center) {
        when (state.keyType) {
            null,
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> ComposableWearSimpleEmulate(
                state,
                keyEmulateUiApi,
                emulateViewModel::onClickEmulate
            )
            FlipperKeyType.INFRARED -> onBack()
            FlipperKeyType.SUB_GHZ -> ComposableWearSubGhzEmulate(
                state,
                keyEmulateUiApi,
                emulateViewModel::onClickEmulate
            )
        }
    }
}
