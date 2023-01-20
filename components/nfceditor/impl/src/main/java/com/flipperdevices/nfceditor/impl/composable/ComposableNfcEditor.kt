package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.length
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.composable.card.ComposableNfcCard
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel
import kotlinx.coroutines.launch

@Composable
fun ComposableNfcEditor(
    nfcEditorViewModel: NfcEditorViewModel,
    nfcEditorState: NfcEditorState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .padding(end = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTypography.current.monoSpaceM14.merge(
                TextStyle(
                    color = LocalPallet.current.text100
                )
            )
        ) {
            val maxIndexSymbolCount = remember(constraints, nfcEditorState) {
                nfcEditorState.sectors.maxOfOrNull { it.lines.maxOf { it.index } }
                    ?.length() ?: 0
            }

            val scaleFactor = key(
                constraints.maxWidth,
                constraints.minWidth,
                maxIndexSymbolCount
            ) {
                calculateScaleFactor(maxIndexSymbolCount)
            }

            SelectionContainer {
                ComposableNfcEditor(
                    nfcEditorState = nfcEditorState,
                    nfcEditorViewModel = nfcEditorViewModel,
                    maxIndexSymbolCount = maxIndexSymbolCount,
                    scaleFactor = scaleFactor
                )
            }
        }
    }
}

@Composable
private fun ComposableNfcEditor(
    nfcEditorViewModel: NfcEditorViewModel,
    nfcEditorState: NfcEditorState,
    maxIndexSymbolCount: Int,
    scaleFactor: Float
) {
    val lazyColumnState = rememberLazyListState()

    var scrollOffset by remember { mutableStateOf(0) }
    var offsetToCenter by remember { mutableStateOf(0) }
    ScrollToActiveCell(
        lazyColumnState,
        nfcEditorViewModel.currentActiveCell,
        nfcEditorState.nfcEditorCardInfo != null,
        scrollOffset - offsetToCenter
    )
    LazyColumn(
        modifier = Modifier.onGloballyPositioned {
            offsetToCenter = it.size.height / 2
        },
        state = lazyColumnState
    ) {
        if (nfcEditorState.nfcEditorCardInfo != null) {
            item {
                ComposableNfcCard(
                    nfcEditorState.nfcEditorCardInfo,
                    scaleFactor,
                    nfcEditorViewModel.currentActiveCell,
                    nfcEditorViewModel::onCellFocus
                )
            }
        }
        items(nfcEditorState.sectors.size) { index ->
            ComposableNfcSector(
                nfcEditorViewModel = nfcEditorViewModel,
                nfcEditorState = nfcEditorState,
                sectorIndex = index,
                maxIndexSymbolCount = maxIndexSymbolCount,
                scaleFactor = scaleFactor,
                onPositionActiveLine = {
                    scrollOffset = it
                }
            )
        }
    }
}

@Composable
private fun ScrollToActiveCell(
    lazyColumnState: LazyListState,
    currentActiveCell: NfcEditorCellLocation?,
    cardInfoPresented: Boolean,
    offset: Int
) {
    val scope = rememberCoroutineScope()

    var previousCell by remember { mutableStateOf<NfcEditorCellLocation?>(null) }
    var previousOffset by remember { mutableStateOf(0) }

    @Suppress("ComplexCondition")
    if (currentActiveCell != null &&
        currentActiveCell != previousCell &&
        offset != previousOffset &&
        !lazyColumnState.isScrollInProgress
    ) {
        previousCell = currentActiveCell
        previousOffset = offset
        LaunchedEffect(key1 = currentActiveCell) {
            scope.launch {
                val newPosition = if (cardInfoPresented) {
                    currentActiveCell.sectorIndex + 1
                } else {
                    currentActiveCell.sectorIndex
                }
                lazyColumnState.animateScrollToItem(newPosition, offset)
            }
        }
    }
}
