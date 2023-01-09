package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickableRipple(
    bounded: Boolean = true,
    onClick: () -> Unit
) = composed {
    this.then(
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = bounded),
            onClick = onClick
        )
    )
}
