package com.flipperdevices.infrared.compose.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredControl
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ComposableInfraredControls(
    controls: ImmutableList<InfraredControl>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(controls.size) { index ->
            ComposableInfraredControl(controls[index])
        }
    }
}

@Composable
private fun ComposableInfraredControl(
    control: InfraredControl,
    modifier: Modifier = Modifier,
) = Unit
