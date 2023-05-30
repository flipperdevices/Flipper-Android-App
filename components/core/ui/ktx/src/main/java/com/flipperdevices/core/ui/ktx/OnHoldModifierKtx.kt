package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.onHoldPress(
    onTap: () -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit
) = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val change = awaitLongPressOrCancellation(down.id)

        val wasLongPress = (change == down)

        if (!wasLongPress) {
            onTap()
            return@awaitEachGesture
        }

        onLongPressStart()
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            if (event.type == PointerEventType.Release) {
                event.changes.forEach { it.consume() }
                onLongPressEnd()
                break
            }
        }
    }
}
