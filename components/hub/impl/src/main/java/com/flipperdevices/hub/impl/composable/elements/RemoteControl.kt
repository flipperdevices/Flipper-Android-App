package com.flipperdevices.hub.impl.composable.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.hub.impl.R

@Composable
fun ComposableRemoteControl(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComposableHubElement(
        iconId = R.drawable.ic_controller,
        onOpen = onOpen,
        titleId = R.string.hub_remote_control_title,
        descriptionId = R.string.hub_remote_control_desc,
        modifier = modifier
    )
}