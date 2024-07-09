package com.flipperdevices.remotecontrols.impl.brands.composable.composable.alphabet

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.unit.dp
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsList
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun <T> AlphabetSearchComposable(
    modifier: Modifier = Modifier,
    items: ImmutableList<T>,
    toHeader: (T) -> Char,
    headers: ImmutableList<Char>,
    listState: LazyListState,
    mainContent: @Composable RowScope.() -> Unit
) {
    val offsets = remember { mutableStateMapOf<Int, Float>() }
    var selectedHeaderIndex by remember { mutableIntStateOf(0) }
    var isScrollingToIndex by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        mainContent()


        fun updateSelectedIndexIfNeeded(offset: Float) {
            if (listState.isScrollInProgress) return
            val index = offsets
                .mapValues { abs(it.value - offset) }
                .entries
                .minByOrNull { it.value }
                ?.key ?: return
            if (selectedHeaderIndex == index) return
            selectedHeaderIndex = index
            val selectedItemIndex = items
                .map { it.let(toHeader).uppercaseChar() }
                .indexOfFirst { char -> char.code == headers[selectedHeaderIndex].code }
                .coerceIn(0 until listState.layoutInfo.totalItemsCount)
            scope.launch {
                isScrollingToIndex = true
                listState.animateScrollToItem(selectedItemIndex)
                isScrollingToIndex = false
            }
        }

        LaunchedEffect(listState.firstVisibleItemIndex, isScrollingToIndex) {
            if (isScrollingToIndex) return@LaunchedEffect
            val ch = items.getOrNull(listState.firstVisibleItemIndex)
                ?.let(toHeader) ?: return@LaunchedEffect
            val i = headers.indexOfFirst { it.uppercaseChar() == ch }
                .coerceIn(0, listState.layoutInfo.totalItemsCount)
            selectedHeaderIndex = i
        }

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures { updateSelectedIndexIfNeeded(it.y) }
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, _ -> updateSelectedIndexIfNeeded(change.position.y) }
                }
        ) {
            headers.forEachIndexed { i, header ->
                HeaderContent(
                    isSelected = i == selectedHeaderIndex,
                    text = header.toString(),
                    onGloballyPositioned = {
                        offsets[i] = it.boundsInParent().center.y
                    }
                )
            }
        }
    }
}

