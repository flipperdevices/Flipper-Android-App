package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.R

@Composable
@Suppress("LongParameterList")
fun SwitchableElement(
    @DrawableRes iconId: Int? = null,
    @StringRes titleId: Int? = null,
    @StringRes descriptionId: Int? = null,
    state: Boolean,
    onSwitchState: (Boolean) -> Unit,
    isCategory: Boolean = false
) {
    Row(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = { onSwitchState(!state) }
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleElement(
            Modifier.weight(weight = 1f),
            iconId,
            titleId,
            descriptionId,
            isCategory = isCategory
        )
        Switch(
            modifier = Modifier.padding(all = 12.dp),
            checked = state, onCheckedChange = onSwitchState,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(R.color.accent),
                uncheckedThumbColor = colorResource(R.color.white_100),
                checkedTrackColor = colorResource(R.color.accent),
                uncheckedTrackColor = colorResource(R.color.black_40),
                uncheckedTrackAlpha = 0.5f
            )
        )
    }
}
