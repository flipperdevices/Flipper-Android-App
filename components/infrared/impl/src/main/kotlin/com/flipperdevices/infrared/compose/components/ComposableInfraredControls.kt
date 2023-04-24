package com.flipperdevices.infrared.compose.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredControl
import com.flipperdevices.keyscreen.api.KeyEmulateApi
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ComposableInfraredControls(
    controls: ImmutableList<InfraredControl>,
    flipperKey: FlipperKey,
    keyEmulateApi: KeyEmulateApi,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(controls.size) { index ->
            val control = controls[index]
            keyEmulateApi.ComposableEmulateInfraredButton(
                modifier = Modifier.padding(horizontal = 24.dp),
                flipperKey = flipperKey,
                name = control.name
            )
        }
    }
}
