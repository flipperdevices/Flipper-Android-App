package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@Suppress("ModifierComposed") // MOB-1039
fun Modifier.clickableRipple(
    bounded: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    this.then(
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = bounded),
            onClick = onClick,
            enabled = enabled
        )
    )
}
