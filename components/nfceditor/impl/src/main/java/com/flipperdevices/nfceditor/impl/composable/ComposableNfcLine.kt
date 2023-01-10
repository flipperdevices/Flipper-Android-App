package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.nfceditor.impl.model.EditorField
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import kotlin.math.roundToInt
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ComposableNfcLine(
    sectorIndex: Int,
    lineIndexInSector: Int,
    visibleIndex: Int,
    line: ImmutableList<NfcEditorCell>,
    maxIndexSymbolCount: Int,
    scaleFactor: Float,
    activeCell: NfcEditorCellLocation? = null,
    onCellFocus: ((NfcEditorCellLocation) -> Unit),
    onPositionActiveLine: ((Int) -> Unit)? = null
) {
    var rowModifier: Modifier = Modifier
    if (onPositionActiveLine != null) {
        rowModifier = rowModifier.onGloballyPositioned {
            onPositionActiveLine(it.positionInParent().y.roundToInt())
        }
    }

    Row(
        modifier = rowModifier
    ) {
        Text(
            modifier = Modifier.width((scaleFactor * WIDTH_LINE_INDEX_DP * maxIndexSymbolCount).dp),
            text = visibleIndex.toString(),
            textAlign = TextAlign.End,
            color = LocalPallet.current.text16,
            fontSize = (scaleFactor * FONT_SIZE_SP).sp,
            maxLines = 1
        )

        line.forEachIndexed { columnIndex, cell ->
            val cellLocation = remember(sectorIndex, lineIndexInSector, columnIndex) {
                NfcEditorCellLocation(EditorField.DATA, sectorIndex, lineIndexInSector, columnIndex)
            }
            ComposableNfcCell(
                cell,
                scaleFactor,
                isActive = cellLocation == activeCell,
                onClick = {
                    onCellFocus.invoke(cellLocation)
                }
            )
        }
    }
}
