package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.impl.R

@Composable
fun ComposableRestore(onClick: () -> Unit) {
    ComposableActionRow(
        iconId = R.drawable.ic_restore,
        descriptionId = R.string.keyscreen_restore_text,
        onClick = onClick
    )
}
