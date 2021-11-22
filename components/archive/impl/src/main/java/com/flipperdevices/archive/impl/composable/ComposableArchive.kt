package com.flipperdevices.archive.impl.composable

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableArchive(connectionBar: @Composable () -> Unit = {}) {
    Scaffold(topBar = {
        connectionBar()
    }) {
    }
}
