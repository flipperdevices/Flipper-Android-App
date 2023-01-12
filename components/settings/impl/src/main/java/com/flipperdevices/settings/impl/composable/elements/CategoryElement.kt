package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun CategoryElement(
    @StringRes titleId: Int,
    state: Boolean,
    modifier: Modifier = Modifier,
    @StringRes descriptionId: Int? = null,
    onSwitchState: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.clickableRipple { onSwitchState(!state) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            Modifier.weight(weight = 1f),
            titleId,
            descriptionId,
            titleTextStyle = LocalTypography.current.buttonB16
        )
        Switch(state = state, onSwitchState = onSwitchState)
    }
}
