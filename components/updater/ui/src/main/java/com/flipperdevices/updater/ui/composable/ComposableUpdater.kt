package com.flipperdevices.updater.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel

@Composable
fun ComposableUpdater(
    updaterViewModel: UpdaterViewModel = viewModel()
) {
    val updateCardState = updaterViewModel.getUpdateCardState().collectAsState()
    Text(text = updateCardState.toString())
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableUpdaterPreview() {
    Column {
        ComposableUpdater()
    }
}
