package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
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
    composition: TextRange?,
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

    if (focusRequester != null) {
        textFieldModifier = textFieldModifier
            .focusRequester(focusRequester)
    }

    val textStyle = key(scaleFactor) {
        LocalTextStyle.current.copy(
            fontSize = (scaleFactor * FONT_SIZE_SP).sp
        )
    }

    if (selection != null && composition != null) {
        BasicTextField(
            modifier = textFieldModifier
                .onFocusChanged {
                    onFocusChanged(it.isFocused)
                },
            value = TextFieldValue(
                cell.content,
                selection,
                TextRange(0, cell.content.length)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(LocalPallet.current.text100),
            onValueChange = onValueChanged
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
