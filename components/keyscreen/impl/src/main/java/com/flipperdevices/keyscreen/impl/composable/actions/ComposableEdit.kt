package com.flipperdevices.keyscreen.impl.composable.actions

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionRow

@Composable
fun ComposableEdit(onClick: () -> Unit) {
    ComposableActionRow(
        iconId = DesignSystem.drawable.ic_edit_icon,
        descriptionId = R.string.keyscreen_edit_text,
        onClick = onClick
    )
}
