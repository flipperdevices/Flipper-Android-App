package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.keyscreen.impl.R

@Composable
fun RowScope.ComposableEmulate(onClick: () -> Unit) {
    ComposableActionFlipper(
        iconId = R.drawable.ic_emulate,
        descriptionId = R.string.keyscreen_emulate,
        onClick = onClick,
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableWritePreview() {
    Row{
        ComposableEmulate(onClick = { })
    }
}
