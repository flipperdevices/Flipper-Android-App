package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SwitchableElement(
    @DrawableRes iconId: Int? = null,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    state: Boolean,
    onSwitchState: (Boolean) -> Unit
) {
    Row() {
        SimpleElement(
            Modifier.weight(weight = 1f),
            iconId,
            titleId,
            descriptionId
        )
        RadioButton(selected = state, onClick = { onSwitchState(!state) })
    }
}
