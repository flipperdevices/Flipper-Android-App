package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel

@Composable
fun ComposableEditTextField(nfcEditorViewModel: NfcEditorViewModel = viewModel()) {
    val nfcEditorState = nfcEditorViewModel.nfcEditorState

    Column {
        nfcEditorState.lines.forEachIndexed { lineIndex, line ->
            Row {
                line.forEachIndexed { columnIndex, cell ->
                    var focusRequester: FocusRequester? = null
                    var textSelection: TextRange? = null
                    val cellLocation = NfcEditorCellLocation(lineIndex, columnIndex)
                    if (nfcEditorState.cursor != null &&
                        nfcEditorState.cursor.location == cellLocation
                    ) {
                        textSelection = TextRange(nfcEditorState.cursor.position)
                        focusRequester = remember { FocusRequester() }
                        LaunchedEffect(nfcEditorState.cursor) {
                            focusRequester.requestFocus()
                        }
                    }
                    ComposableNfcCell(
                        nfcEditorViewModel,
                        focusRequester,
                        cellLocation,
                        cell,
                        textSelection
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposableNfcCell(
    nfcEditorViewModel: NfcEditorViewModel,
    focusRequester: FocusRequester?,
    nfcEditorCellLocation: NfcEditorCellLocation,
    cell: NfcEditorCell,
    selection: TextRange?
) {
    var textFieldModifier = Modifier
        .width(IntrinsicSize.Min)
        .padding(start = 16.dp)

    if (focusRequester != null) {
        textFieldModifier = textFieldModifier
            .focusRequester(focusRequester)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    var keyboardIsOpen by remember { mutableStateOf(false) }

    LaunchedEffect(keyboardIsOpen) {
        if (keyboardIsOpen) {
            keyboardController?.hide()
        }
        keyboardIsOpen = false
    }

    BasicTextField(
        modifier = textFieldModifier.onFocusChanged {
            when (it.isFocused) {
                true -> nfcEditorViewModel.currentActiveCell = nfcEditorCellLocation
                false -> if (nfcEditorViewModel.currentActiveCell == nfcEditorCellLocation) {
                    nfcEditorViewModel.currentActiveCell = null
                }
            }
        },
        value = TextFieldValue(
            cell.content,
            selection ?: TextRange.Zero
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.h4,
        onValueChange = {
            nfcEditorViewModel.onChangeSelection(nfcEditorCellLocation, it.selection.start)
        }
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableEditTextFieldPreview() {
    Box {
        ComposableEditTextField()
    }
}
