package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.keyscreen.impl.model.KeyScreenState

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableKeyScreen(keyScreenState: KeyScreenState = KeyScreenState.Initial) {
    when (keyScreenState) {
        KeyScreenState.Initial -> ComposableKeyInitial()
        is KeyScreenState.Error -> TODO()
        is KeyScreenState.Ready -> TODO()
    }
}

@Composable
private fun ComposableKeyInitial() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}
