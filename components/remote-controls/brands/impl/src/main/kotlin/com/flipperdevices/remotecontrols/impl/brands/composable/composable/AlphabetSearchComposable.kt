package com.flipperdevices.remotecontrols.impl.brands.composable.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun AlphabetSearchComposable(
    model: BrandsDecomposeComponent.Model.Loaded,
    modifier: Modifier = Modifier,
    onBrandClicked: (BrandModel) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val listState = rememberLazyListState()
        BrandsList(
            modifier = Modifier.weight(1f),
            listState = listState,
            brands = model.groupedBrands.flatMap { it.second },
            onBrandClicked = onBrandClicked
        )

        val offsets = remember { mutableStateMapOf<Int, Float>() }
        var selectedHeaderIndex by remember { mutableIntStateOf(0) }
        var isScrollingToIndex by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        fun updateSelectedIndexIfNeeded(offset: Float) {
            if (listState.isScrollInProgress) return
            val index = offsets
                .mapValues { abs(it.value - offset) }
                .entries
                .minByOrNull { it.value }
                ?.key ?: return
            if (selectedHeaderIndex == index) return
            selectedHeaderIndex = index
            val selectedItemIndex = model
                .brands
                .map { it.name.first().uppercaseChar() }
                .indexOfFirst { char -> char.code == model.headers[selectedHeaderIndex].code }
                .coerceIn(0 until listState.layoutInfo.totalItemsCount)
            scope.launch {
                isScrollingToIndex = true
                listState.animateScrollToItem(selectedItemIndex)
                isScrollingToIndex = false
            }
        }

        LaunchedEffect(listState.firstVisibleItemIndex, isScrollingToIndex) {
            if (isScrollingToIndex) return@LaunchedEffect
            val ch = model.brands.getOrNull(listState.firstVisibleItemIndex)
                ?.name
                ?.first() ?: return@LaunchedEffect
            val i = model.headers.indexOfFirst { it.uppercaseChar() == ch }
                .coerceIn(0, listState.layoutInfo.totalItemsCount)
            selectedHeaderIndex = i
        }

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures {
                        updateSelectedIndexIfNeeded(it.y)
                    }
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, _ ->
                        updateSelectedIndexIfNeeded(change.position.y)
                    }
                }
        ) {
            model.headers.forEachIndexed { i, header ->
                val isSelected = i == selectedHeaderIndex
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
                    text = "$header",
                    modifier = Modifier.onGloballyPositioned {
                        offsets[i] = it.boundsInParent().center.y
                    }.scale(scale),
                    textAlign = TextAlign.Center,
                    style = LocalTypography.current.subtitleM12,
                    color = textColor
                )
            }
        }
    }
}
