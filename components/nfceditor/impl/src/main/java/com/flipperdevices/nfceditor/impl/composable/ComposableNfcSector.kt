package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel

@Composable
fun ComposableNfcSector(
    nfcEditorViewModel: NfcEditorViewModel,
    nfcEditorState: NfcEditorState,
    sectorIndex: Int,
    maxIndexSymbolCount: Int,
    scaleFactor: Float
) {
    Column {
        nfcEditorState.sectors[sectorIndex].lines.forEachIndexed { lineIndex, line ->
            ComposableNfcLine(
                sectorIndex = sectorIndex,
                lineIndexInSector = lineIndex,
                visibleIndex = line.index,
                line = line.cells,
                maxIndexSymbolCount = maxIndexSymbolCount,
                scaleFactor = scaleFactor,
                cursor = nfcEditorState.cursor,
                onFocusChanged = { cellLocation, isFocused ->
                    when (isFocused) {
                        true -> nfcEditorViewModel.currentActiveCell = cellLocation
                        false -> if (nfcEditorViewModel.currentActiveCell == cellLocation) {
                            nfcEditorViewModel.currentActiveCell = null
                        }
                    }
                },
                onValueChanged = { location, textValue ->
                    nfcEditorViewModel.onChangeText(
                        textValue.text,
                        location,
                        textValue.selection.start
                    )
                }
            )
        }
    }
}
