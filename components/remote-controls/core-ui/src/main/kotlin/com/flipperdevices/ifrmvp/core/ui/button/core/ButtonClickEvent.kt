package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.onScrollHoldPress

enum class ButtonClickEvent {
    SINGLE_CLICK, HOLD, RELEASE
}
fun Modifier.onScrollHoldPress(
    onClick: (ButtonClickEvent) -> Unit,
) = this.then(
    Modifier.onScrollHoldPress(
        onTap = { onClick.invoke(ButtonClickEvent.SINGLE_CLICK) },
        onLongPressStart = { onClick.invoke(ButtonClickEvent.HOLD) },
        onLongPressEnd = { onClick.invoke(ButtonClickEvent.RELEASE) }
    )
)
