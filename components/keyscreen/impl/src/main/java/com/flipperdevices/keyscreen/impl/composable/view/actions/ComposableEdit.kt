package com.flipperdevices.keyscreen.impl.composable.view.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.impl.R

@Composable
fun ComposableEdit(onClick: () -> Unit) {
    ComposableActionRow(
        iconId = R.drawable.ic_edit_icon,
        descriptionId = R.string.keyscreen_edit_text,
        onClick = onClick
    )
}
