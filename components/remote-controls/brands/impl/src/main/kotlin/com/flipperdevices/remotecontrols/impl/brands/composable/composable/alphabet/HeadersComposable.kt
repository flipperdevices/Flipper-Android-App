package com.flipperdevices.remotecontrols.impl.brands.composable.composable.alphabet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.launch

@Composable
internal fun HeaderComposable(
    isSelected: Boolean,
    text: String,
    onGloballyPosition: (LayoutCoordinates) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when (isSelected) {
            true -> 1.5f
            false -> 1f
        }
    )
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> LocalPalletV2.current.text.body.primary
            else -> LocalPalletV2.current.text.body.secondary
        }
    )
    Text(
        text = text,
        modifier = modifier
            .onGloballyPositioned(onGloballyPosition)
            .scale(scale),
        textAlign = TextAlign.Center,
        style = LocalTypography.current.subtitleM12,
        color = textColor
    )
}

@Composable
internal fun <T> HeadersComposable(
    scrollState: ScrollState<T>,
    listState: LazyListState,
    headers: ImmutableSet<Char>
) {
    val scope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures {
                    scope.launch { scrollState.updateSelectedIndexIfNeeded(it.y, listState) }
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, _ ->
                    scope.launch {
                        scrollState.updateSelectedIndexIfNeeded(
                            change.position.y,
                            listState
                        )
                    }
                }
            }
    ) {
        val selectedHeaderIndex = scrollState.selectedHeaderIndex
        headers.forEachIndexed { i, header ->
            HeaderComposable(
                isSelected = i == selectedHeaderIndex,
                text = header.toString(),
                onGloballyPosition = {
                    scrollState.updateOffset(i, it.boundsInParent().center.y)
                }
            )
        }
    }
}
