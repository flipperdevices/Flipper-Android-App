package com.flipperdevices.archive.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.archive.impl.composable.tab.ComposableTabs

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableArchive(connectionBar: @Composable () -> Unit = {}) {
    Scaffold(topBar = {
        connectionBar()
    }) {
        Box(modifier = Modifier.padding(it)) {
            ComposableTabs()
        }
    }
}
