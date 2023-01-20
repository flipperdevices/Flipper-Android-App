package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionRow
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableRestore(onClick: () -> Unit) {
    ComposableActionRow(
        iconId = DesignSystem.drawable.ic_restore,
        descriptionId = R.string.keyscreen_restore_text,
        onClick = onClick
    )
}
