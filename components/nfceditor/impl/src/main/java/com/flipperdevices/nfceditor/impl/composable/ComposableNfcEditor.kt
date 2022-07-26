package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel

@Composable
fun ComposableNfcEditor(nfcEditorViewModel: NfcEditorViewModel = viewModel()) {
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTypography.current.monoSpaceM14.merge(
                TextStyle(
                    color = LocalPallet.current.text100
                )
            )
        ) {
            val scaleFactor = calculateScaleFactor()

            ComposableNfcEditor(nfcEditorViewModel = nfcEditorViewModel, scaleFactor = scaleFactor)
        }
    }
}

@Composable
private fun ComposableNfcEditor(nfcEditorViewModel: NfcEditorViewModel, scaleFactor: Float) {
    val nfcEditorState = nfcEditorViewModel.nfcEditorState

    Column {
        nfcEditorState.lines.forEachIndexed { lineIndex, line ->
            Row {
                ComposableNfcLine(
                    lineIndex,
                    line,
                    scaleFactor,
                    nfcEditorState.cursor,
                    onFocusChanged = { cellLocation, isFocused ->
                        when (isFocused) {
                            true -> nfcEditorViewModel.currentActiveCell = cellLocation
                            false -> if (nfcEditorViewModel.currentActiveCell == cellLocation) {
                                nfcEditorViewModel.currentActiveCell = null
                            }
                        }
                    },
                    onValueChanged = { location, textValue ->
                        nfcEditorViewModel.onChangeSelection(
                            location,
                            textValue.selection.start
                        )
                    }
                )
            }
        }
    }
}
