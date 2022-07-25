package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell

const val FONT_SIZE_SP = 14
const val PADDING_CELL_DP = 6

@Composable
fun ComposableNfcCell(
    focusRequester: FocusRequester?,
    cell: NfcEditorCell,
    scaleFactor: Float,
    selection: TextRange?,
    onFocusChanged: (isFocused: Boolean) -> Unit,
    onValueChanged: (TextFieldValue) -> Unit
) {
    var textFieldModifier = Modifier
        .width(IntrinsicSize.Min)
        .padding(start = (scaleFactor * PADDING_CELL_DP).dp)

    if (focusRequester != null) {
        textFieldModifier = textFieldModifier
            .focusRequester(focusRequester)
    }

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

    BasicTextField(
        modifier = textFieldModifier.onFocusChanged {
            onFocusChanged(it.isFocused)
        },
        value = TextFieldValue(
            cell.content,
            selection ?: lastSelection
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = (scaleFactor * FONT_SIZE_SP).sp
        ),
        cursorBrush = SolidColor(LocalPallet.current.text100),
        onValueChange = onValueChanged
    )
}
