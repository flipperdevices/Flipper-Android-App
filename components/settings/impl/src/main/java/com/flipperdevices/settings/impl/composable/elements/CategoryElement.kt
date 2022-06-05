package com.flipperdevices.settings.impl.composable.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CategoryElement(
    @StringRes titleId: Int,
    @StringRes descriptionId: Int? = null,
    state: Boolean,
    onSwitchState: (Boolean) -> Unit
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
            titleId,
            descriptionId,
            titleTextStyle = TextStyle(
                fontWeight = FontWeight.W700,
                fontSize = 16.sp
            )
        )
        Switch(state = state, onSwitchState = onSwitchState)
    }
}
