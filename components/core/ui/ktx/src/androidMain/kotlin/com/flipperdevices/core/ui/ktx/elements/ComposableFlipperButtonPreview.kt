package com.flipperdevices.core.ui.ktx.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperButtonPreview() {
    Column {
        ComposableFlipperButton(text = "Test")
    }
}
