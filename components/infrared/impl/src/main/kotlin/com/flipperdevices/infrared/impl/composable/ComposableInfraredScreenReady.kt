package com.flipperdevices.infrared.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.infrared.impl.composable.components.ComposableInfraredRemotes
import com.flipperdevices.infrared.impl.model.InfraredTab
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyscreen.model.KeyScreenState

@Composable
internal fun ComposableInfraredScreenReady(
    currentTab: InfraredTab,
    state: KeyScreenState.Ready,
    keyCardContent: @Composable () -> Unit,
    keyEmulateContent: @Composable (EmulateConfig) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentTab) {
            InfraredTab.INFO -> keyCardContent()
            InfraredTab.REMOTE -> ComposableInfraredRemotes(state, keyEmulateContent)
        }
    }
}
