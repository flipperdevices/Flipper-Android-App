package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.nfceditor.impl.model.NfcCellType
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.viewmodel.BYTES_SYMBOL_COUNT

const val FONT_SIZE_SP = 14
const val PADDING_CELL_DP = 3

@Composable
fun ComposableNfcCell(
    cell: NfcEditorCell,
    scaleFactor: Float,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val paddingDp = remember(scaleFactor) {
        (scaleFactor * PADDING_CELL_DP).dp
    }

    val textFieldModifier = Modifier
        .clickable(onClick = onClick)
        .padding(start = paddingDp, end = paddingDp)

    val textColor = when (cell.cellType) {
        NfcCellType.SIMPLE -> LocalPallet.current.text100
        NfcCellType.UID -> LocalPallet.current.nfcCardUIDColor
        NfcCellType.KEY_A -> LocalPallet.current.nfcCardKeyAColor
        NfcCellType.ACCESS_BITS -> LocalPallet.current.nfcCardAccessBitsColor
        NfcCellType.KEY_B -> LocalPallet.current.nfcCardKeyBColor
        NfcCellType.ON_CARD -> LocalPallet.current.onNfcCard
    }

    val textStyle = key(cell.cellType, scaleFactor) {
        LocalTextStyle.current.merge(
            TextStyle(
                fontSize = (scaleFactor * FONT_SIZE_SP).sp,
                color = textColor
            )
        )
    }

    var text = cell.content

    if (text.length < BYTES_SYMBOL_COUNT) {
        text += " ".repeat(BYTES_SYMBOL_COUNT - text.length)
    }

    if (isActive) {
        Box(
            modifier
                .clip(RoundedCornerShape(2.dp))
                .background(LocalPallet.current.substrateActiveCellNfcEditor)
        ) {
            ComposableNfcCellText(
                text,
                textStyle,
                modifier = textFieldModifier
            )
        }
        return
    }
    ComposableNfcCellText(
        modifier = textFieldModifier,
        text = text,
        textStyle = textStyle
    )
}

@Composable
private fun ComposableNfcCellText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = textStyle,
        maxLines = 1
    )
}
