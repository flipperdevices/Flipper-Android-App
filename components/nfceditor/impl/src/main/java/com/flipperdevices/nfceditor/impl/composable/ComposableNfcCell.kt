package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.nfceditor.impl.model.NfcCellType
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell

const val FONT_SIZE_SP = 14
const val PADDING_CELL_DP = 6

@Composable
fun ComposableNfcCell(
    focusRequester: FocusRequester?,
    cell: NfcEditorCell,
    scaleFactor: Float,
    selection: TextRange?,
    isEditable: Boolean,
    onFocusChanged: (isFocused: Boolean) -> Unit,
    onValueChanged: (TextFieldValue) -> Unit
) {
    val paddingDp = remember(scaleFactor) {
        (scaleFactor * PADDING_CELL_DP).dp
    }
    val widthDp = remember(scaleFactor) {
        (scaleFactor * 2 * WIDTH_LINE_INDEX_DP).dp
    }

    var textFieldModifier = Modifier
        .padding(start = paddingDp)
        .width(widthDp)

    val textColor = when (cell.cellType) {
        NfcCellType.SIMPLE -> LocalPallet.current.text100
        NfcCellType.UID -> LocalPallet.current.nfcCardUIDColor
        NfcCellType.KEY_A -> LocalPallet.current.nfcCardKeyAColor
        NfcCellType.ACCESS_BITS -> LocalPallet.current.nfcCardAccessBitsColor
        NfcCellType.KEY_B -> LocalPallet.current.nfcCardKeyBColor
    }

    if (focusRequester != null) {
        textFieldModifier = textFieldModifier
            .focusRequester(focusRequester)
    }

    val textStyle = key(cell.cellType, scaleFactor) {
        LocalTextStyle.current.merge(
            TextStyle(
                fontSize = (scaleFactor * FONT_SIZE_SP).sp,
                color = textColor
            )
        )
    }

    if (isEditable) {
        ComposableNfcCellEditable(
            textFieldModifier.onFocusChanged {
                onFocusChanged(it.isFocused)
            },
            cell,
            selection,
            textStyle,
            onValueChanged
        )
        return
    }

    Text(
        modifier = textFieldModifier.clickable {
            onFocusChanged(true)
        },
        text = cell.content,
        style = textStyle
    )
}

@Composable
fun ComposableNfcCellEditable(
    modifier: Modifier,
    cell: NfcEditorCell,
    selection: TextRange?,
    textStyle: TextStyle,
    onValueChanged: (TextFieldValue) -> Unit
) {
    /**
     * This hack is needed so that the cursor doesn't jump into the frame
     * when we haven't yet called focusRequester.requestFocus().
     * Otherwise, the user will see an incorrect cursor display for a moment.
     *
     * Example if we use TextRange.Zero as default:
     * Cursor(0,0,1):
     * [0|0] [00]
     *
     * -> Press any button
     * Cursor(0, 1, 0):
     * [|00] [00]
     *
     * -> focusRequester.requestFocus()
     * [00] [|00]
     *
     * Example if we use lastSelection as default:
     * [0|0] [00]
     * -> Press any button
     * [0|0] [00]
     * -> focusRequester.requestFocus()
     * [00] [|00]
     */
    var lastSelection by remember { mutableStateOf(TextRange.Zero) }
    if (selection != null) {
        lastSelection = selection
    }
    var composition by remember { mutableStateOf<TextRange?>(TextRange(0, cell.content.length)) }

    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(
            cell.content,
            selection ?: lastSelection,
            composition
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(LocalPallet.current.text100),
        onValueChange = {
            if (composition != it.composition) {
                composition = it.composition
            }
            onValueChanged(it)
        }
    )
}
