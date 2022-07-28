package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.flipperdevices.core.ktx.jre.length
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.nfceditor.impl.composable.card.ComposableNfcCard
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel

@Composable
fun ComposableNfcEditor(nfcEditorViewModel: NfcEditorViewModel, nfcEditorState: NfcEditorState) {
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTypography.current.monoSpaceM14.merge(
                TextStyle(
                    color = LocalPallet.current.text100
                )
            )
        ) {
            val maxIndexSymbolCount = remember(constraints, nfcEditorState) {
                nfcEditorState.sectors.filterNotNull().maxOfOrNull { it.lines.maxOf { it.index } }
                    ?.length() ?: 0
            }

            val scaleFactor = key(maxIndexSymbolCount) {
                calculateScaleFactor(maxIndexSymbolCount)
            }

            SelectionContainer {
                ComposableNfcEditor(
                    nfcEditorState = nfcEditorState,
                    nfcEditorViewModel = nfcEditorViewModel,
                    maxIndexSymbolCount = maxIndexSymbolCount,
                    scaleFactor = scaleFactor
                )
            }
        }
    }
}

@Composable
private fun ComposableNfcEditor(
    nfcEditorViewModel: NfcEditorViewModel,
    nfcEditorState: NfcEditorState,
    maxIndexSymbolCount: Int,
    scaleFactor: Float
) {
    LazyColumn {
        if (nfcEditorState.nfcEditorCardInfo != null) {
            item(nfcEditorState.nfcEditorCardInfo.hashCode()) {
                ComposableNfcCard(nfcEditorState.nfcEditorCardInfo)
            }
        }
        items(nfcEditorState.sectors.size, key = { it.hashCode() }) { index ->
            ComposableNfcSector(
                nfcEditorViewModel = nfcEditorViewModel,
                nfcEditorState = nfcEditorState,
                sectorIndex = index,
                maxIndexSymbolCount = maxIndexSymbolCount,
                scaleFactor = scaleFactor
            )
        }
    }
}
