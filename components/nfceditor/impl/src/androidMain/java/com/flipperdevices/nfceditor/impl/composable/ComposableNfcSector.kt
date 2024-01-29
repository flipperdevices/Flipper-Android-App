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
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorState

@Composable
@Suppress("MagicNumber")
fun ComposableNfcSector(
    nfcEditorState: NfcEditorState,
    sectorIndex: Int,
    maxIndexSymbolCount: Int,
    scaleFactor: Float,
    onCellFocus: (NfcEditorCellLocation?) -> Unit,
    currentActiveCell: NfcEditorCellLocation?,
    modifier: Modifier = Modifier,
    onPositionActiveLine: (Int) -> Unit,
) {
    Column(modifier = modifier) {
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

        currentSector.lines.forEachIndexed { lineIndex, line ->

            ComposableNfcLine(
                sectorIndex = sectorIndex,
                lineIndexInSector = lineIndex,
                visibleIndex = line.index,
                line = line.cells,
                maxIndexSymbolCount = maxIndexSymbolCount,
                scaleFactor = scaleFactor,
                activeCell = currentActiveCell,
                onCellFocus = onCellFocus,
                onPositionActiveLine = if (currentActiveCell?.sectorIndex == sectorIndex &&
                    currentActiveCell.lineIndex == lineIndex
                ) {
                    onPositionActiveLine
                } else {
                    null
                }
            )
        }
    }
}
