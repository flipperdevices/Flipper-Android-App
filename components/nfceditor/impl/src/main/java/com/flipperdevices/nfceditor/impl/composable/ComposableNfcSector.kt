package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel

@Composable
@Suppress("MagicNumber")
fun ComposableNfcSector(
    nfcEditorViewModel: NfcEditorViewModel,
    nfcEditorState: NfcEditorState,
    sectorIndex: Int,
    maxIndexSymbolCount: Int,
    scaleFactor: Float
) {
    Column {
        Text(
            modifier = Modifier.padding(
                top = (scaleFactor * 24).dp,
                start = (scaleFactor * WIDTH_LINE_INDEX_DP * maxIndexSymbolCount).dp,
                end = (scaleFactor * 24).dp
            ),
            text = stringResource(R.string.nfceditor_sector_title, sectorIndex),
            color = LocalPallet.current.text100,
            fontSize = (scaleFactor * 12).sp,
            style = LocalTypography.current.subtitleB12
        )

        val currentSector = nfcEditorState.sectors[sectorIndex]

        if (currentSector != null) {
            currentSector.lines.forEachIndexed { lineIndex, line ->
                val isNonEditableLine = sectorIndex == 0 && lineIndex == 0

                ComposableNfcLine(
                    sectorIndex = sectorIndex,
                    lineIndexInSector = lineIndex,
                    visibleIndex = line.index,
                    line = line.cells,
                    maxIndexSymbolCount = maxIndexSymbolCount,
                    scaleFactor = scaleFactor,
                    cursor = nfcEditorViewModel.nfcEditorCursor,
                    onFocusChanged = if (isNonEditableLine) null
                    else nfcEditorViewModel::onCellFocus,
                    onValueChanged = if (isNonEditableLine) null else {
                        { location, textValue ->
                            nfcEditorViewModel.onChangeText(
                                textValue.text,
                                location,
                                textValue.selection.start
                            )
                        }
                    }
                )
            }
        } else {
            Text(
                text = stringResource(R.string.nfceditor_sector_broke),
                color = LocalPallet.current.text40,
                fontSize = (scaleFactor * 14).sp,
                style = LocalTypography.current.subtitleB12
            )
        }
    }
}
