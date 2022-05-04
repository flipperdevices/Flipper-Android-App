package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.keyscreen.impl.R

@Composable
fun ComposableWrite(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ComposableActionFlipper(
        iconId = R.drawable.ic_write,
        descriptionId = R.string.keyscreen_write,
        onClick = onClick,
        modifier = modifier
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
        ComposableWrite(onClick = { })
    }
}
