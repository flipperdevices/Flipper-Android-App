package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.FlipperDeviceState
import com.flipperdevices.keyscreen.impl.viewmodel.FlipperDeviceViewModel

@Composable
fun ComposableEmulate(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val flipperDeviceViewModel = viewModel<FlipperDeviceViewModel>()
    val flipperDeviceState by flipperDeviceViewModel.getFlipperDeviceState().collectAsState()
    val enabled = flipperDeviceState == FlipperDeviceState.CONNECTED

    ComposableActionFlipperHorizontal(
        modifier = modifier,
        iconId = DesignSystem.drawable.ic_emulate,
        descriptionId = R.string.keyscreen_emulate,
        onClick = if (enabled) onClick else null
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableWritePreview() {
    Row {
        ComposableEmulate(onClick = { })
    }
}
