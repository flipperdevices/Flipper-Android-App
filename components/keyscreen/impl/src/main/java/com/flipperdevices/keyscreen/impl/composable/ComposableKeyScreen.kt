package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.impl.model.KeyScreenState

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableKeyScreen(keyScreenState: KeyScreenState = KeyScreenState.InProgress) {
    when (keyScreenState) {
        KeyScreenState.InProgress -> ComposableKeyInitial()
        is KeyScreenState.Error -> ComposableKeyError(keyScreenState)
        is KeyScreenState.Ready -> ComposableKeyParsed(keyScreenState.parsedKey)
    }
}

@Composable
private fun ComposableKeyInitial() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ComposableKeyError(error: KeyScreenState.Error) {
    val errorText = stringResource(error.reason)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorText,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ComposableKeyParsed(keyParsed: FlipperKeyParsed) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ComposableKeyCard(keyParsed)
    }
}
