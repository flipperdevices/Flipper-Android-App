package com.flipperdevices.pair.impl.composable.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ComposableAgreeButton(
    text: String,
    onPressListener: () -> Unit
) {
    TextButton(
        onClick = onPressListener,
        modifier = Modifier
            .padding(all = 16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Blue,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ComposableBackButton(
    text: String,
    onPressListener: () -> Unit
) {
    TextButton(
        onClick = onPressListener,
        modifier = Modifier
            .padding(all = 16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.Blue
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun PreviewComposableAgreeButton() {
    Row {
        ComposableBackButton(text = "Back") {}
        ComposableAgreeButton(text = "TestButton") {}
    }
}
