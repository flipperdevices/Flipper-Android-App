@file:Suppress("MatchingDeclarationName")

package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class RevealState {
    var isRevealed = mutableStateOf(false)
    var contextMenuWidth = mutableFloatStateOf(0f)
    val offset = Animatable(initialValue = 0f)

    suspend fun onDragEnd() {
        when {
            // right to left
            offset.value <= -contextMenuWidth.floatValue / 2f -> {
                offset.animateTo(-contextMenuWidth.floatValue)
                isRevealed.value = true
            }
            // left to right
            offset.value >= contextMenuWidth.floatValue / 2f -> {
                offset.animateTo(contextMenuWidth.floatValue)
                isRevealed.value = true
            }

            else -> {
                offset.animateTo(0f)
                isRevealed.value = false
            }
        }
    }

    // Currently only right to left
    suspend fun onHorizontalDrag(dragAmount: Float) {
        val newOffset = (offset.value + dragAmount)
            .coerceIn(-contextMenuWidth.floatValue, 0f)
        offset.snapTo(newOffset)
    }

    suspend fun animateHide() {
        offset.animateTo(0f)
    }

    private suspend fun animateRtlRevel() {
        offset.animateTo(-contextMenuWidth.floatValue)
    }

    private suspend fun animateLtrRevel() {
        offset.animateTo(contextMenuWidth.floatValue)
    }

    suspend fun animateReveal() {
        if (isRevealed.value) {
            if (offset.value < 0) {
                animateRtlRevel()
            } else {
                animateLtrRevel()
            }
        } else {
            animateHide()
        }
    }

    fun onActionsSizeChange(size: IntSize) {
        contextMenuWidth.floatValue = size.width.toFloat()
    }
}

@Composable
fun rememberRevealState(): RevealState {
    val state = remember { RevealState() }
    LaunchedEffect(key1 = state.isRevealed.value, state.contextMenuWidth.floatValue) {
        state.animateReveal()
    }
    return state
}

@Composable
fun SwipeToReveal(
    revealState: RevealState,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .onSizeChanged(revealState::onActionsSizeChange)
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(revealState.offset.value.roundToInt(), 0) }
                .pointerInput(revealState.contextMenuWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch { revealState.onHorizontalDrag(dragAmount) }
                        },
                        onDragEnd = {
                            scope.launch { revealState.onDragEnd() }
                        }
                    )
                },
            content = { content() },
            color = Color.Transparent,
            contentColor = Color.Transparent
        )
    }
}
