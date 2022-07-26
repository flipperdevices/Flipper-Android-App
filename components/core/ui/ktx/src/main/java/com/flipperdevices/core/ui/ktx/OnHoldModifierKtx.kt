package com.flipperdevices.core.ui.ktx

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

fun Modifier.onHoldPress(
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
    onClick: () -> Unit = {}
) =
    pointerInput(Unit) {
        forEachGesture {
            val down = awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = false)
            }
            val change = awaitLongPressOrCancellation(down)
            val wasLongPress = (change == down)
            if (!wasLongPress) {
                onClick.invoke()
                return@forEachGesture
            }
            onLongPressStart()
            awaitPointerEventScope { waitForUpOrCancellation() }
            onLongPressEnd()
        }
    }

// Copied from internal function in Compose Source Code
suspend fun PointerInputScope.awaitLongPressOrCancellation(
    initialDown: PointerInputChange
): PointerInputChange? {
    var longPress: PointerInputChange? = null
    var currentDown = initialDown
    val longPressTimeout = viewConfiguration.longPressTimeoutMillis
    return try {
        // wait for first tap up or long press
        withTimeout(longPressTimeout) {
            awaitPointerEventScope {
                var finished = false
                while (!finished) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.changes.all { it.changedToUpIgnoreConsumed() }) {
                        // All pointers are up
                        finished = true
                    }

                    if (
                        event.changes.any {
                            it.consumed.downChange || it.isOutOfBounds(size, extendedTouchPadding)
                        }
                    ) {
                        finished = true // Canceled
                    }

                    // Check for cancel by position consumption. We can look on the Final pass of
                    // the existing pointer event because it comes after the Main pass we checked
                    // above.
                    val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                    if (consumeCheck.changes.any { it.positionChangeConsumed() }) {
                        finished = true
                    }
                    if (event.changes.firstOrNull { it.id == currentDown.id }?.pressed == true) {
                        longPress = event.changes.firstOrNull { it.id == currentDown.id }
                    } else {
                        val newPressed = event.changes.firstOrNull { it.pressed }
                        if (newPressed != null) {
                            currentDown = newPressed
                            longPress = currentDown
                        } else {
                            // should technically never happen as we checked it above
                            finished = true
                        }
                    }
                }
            }
        }
        null
    } catch (_: TimeoutCancellationException) {
        longPress ?: initialDown
    }
}
