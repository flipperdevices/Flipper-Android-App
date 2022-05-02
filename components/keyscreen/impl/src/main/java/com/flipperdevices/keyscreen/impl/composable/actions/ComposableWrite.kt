package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.keyscreen.impl.R

@Composable
fun RowScope.ComposableWrite(onClick: () -> Unit) {
    ComposableActionFlipper(
        iconId = R.drawable.ic_write,
        descriptionId = R.string.keyscreen_write,
        onClick = onClick
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableWritePreview() {
    Row {
        ComposableWrite(onClick = { })
    }

}