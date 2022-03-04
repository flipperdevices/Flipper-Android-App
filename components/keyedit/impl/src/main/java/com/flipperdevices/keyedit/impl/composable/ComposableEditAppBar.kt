package com.flipperdevices.keyedit.impl.composable

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.keyedit.impl.R
import com.flipperdevices.keyedit.impl.model.SaveButtonState
import com.flipperdevices.keyscreen.shared.bar.ComposableBarSimpleText
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar

@Composable
fun ComposableEditAppBar(
    saveButtonState: SaveButtonState,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    ComposableKeyScreenAppBar(
        startBlock = {
            ComposableBarSimpleText(it, R.string.keyedit_bar_cancel, onClick = onBack)
        },
        centerBlock = {
            ComposableBarTitle(modifier = it, textId = R.string.keyedit_bar_title)
        },
        endBlock = {
            when (saveButtonState) {
                SaveButtonState.ENABLED -> ComposableBarSimpleText(
                    modifier = it,
                    textId = R.string.keyedit_bar_save,
                    colorId = DesignSystem.color.accent_secondary,
                    onClick = onSave
                )
                SaveButtonState.DISABLED -> ComposableBarSimpleText(
                    modifier = it,
                    textId = R.string.keyedit_bar_save
                )
                SaveButtonState.IN_PROGRESS -> CircularProgressIndicator()
            }
        }
    )
}
