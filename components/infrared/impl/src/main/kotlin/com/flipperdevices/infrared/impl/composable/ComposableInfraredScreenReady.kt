package com.flipperdevices.infrared.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.infrared.impl.composable.components.ComposableInfraredRemotes
import com.flipperdevices.infrared.impl.viewmodel.InfraredEmulateState
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyscreen.model.KeyScreenState

@Composable
internal fun ComposableInfraredScreenReady(
    state: KeyScreenState.Ready,
    emulateState: InfraredEmulateState?,
    keyCardContent: @Composable () -> Unit,
    keyEmulateContent: @Composable (EmulateConfig) -> Unit,
    keyEmulateErrorContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keyCardContent()
        when (emulateState) {
            InfraredEmulateState.UPDATE_FLIPPER,
            InfraredEmulateState.NOT_CONNECTED -> keyEmulateErrorContent()
            InfraredEmulateState.CONNECTING,
            InfraredEmulateState.SYNCING,
            InfraredEmulateState.ALL_GOOD, null -> ComposableInfraredRemotes(state, keyEmulateContent)
        }
    }
}
