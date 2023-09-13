package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.onHoldPress(
    onTap: () -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit
) = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val change = awaitLongPressOrCancellation(down.id)

        val wasLongPress = change != null

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

private const val DELAY_SCROLL = 200L

fun Modifier.onScrollHoldPress(
    onTap: () -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit
) = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)

        val nextEvent = withTimeoutOrNull(DELAY_SCROLL) {
            awaitPointerEvent(PointerEventPass.Main)
        }

        if (nextEvent != null) {
            val isStillHold = nextEvent.changes.all { it.position.y == down.position.y }
            if (isStillHold.not()) {
                return@awaitEachGesture
            }
        }

        val change = awaitLongPressOrCancellation(down.id)
        if (change == null) {
            if (currentEvent.isPointerUp(down.id)) {
                onTap()
            }
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

private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean {
    return changes.firstOrNull { it.id == pointerId }?.pressed != true
}
