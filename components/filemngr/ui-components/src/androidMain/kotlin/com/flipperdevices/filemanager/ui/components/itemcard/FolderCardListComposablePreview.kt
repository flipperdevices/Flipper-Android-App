package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import flipperapp.components.filemngr.ui_components.generated.resources.ic_folder_black
import org.jetbrains.compose.resources.painterResource
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Preview
@Composable
private fun FolderCardListComposablePreview() {
    FlipperThemeInternal {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ItemUiSelectionState.entries.forEach { selectionState ->
                SwipeToDismissFolderCardListComposable(
                    painter = painterResource(FR.drawable.ic_folder_black),
                    title = "Short title",
                    subtitle = "Short SubTitle",
                    selectionState = selectionState,
                    canDeleteFiles = true,
                    onClick = {},
                    onCheckChange = {},
                    onMoreClick = {},
                    onDelete = {}
                )
            }
            ItemUiSelectionState.entries.forEach { selectionState ->
                SwipeToDismissFolderCardListComposable(
                    painter = rememberVectorPainter(Icons.Filled.Folder),
                    title = "A very very ultra mega super duper log title with some message at the end",
                    subtitle = "A very very ultra mega super duper log title with some message at the end",
                    selectionState = selectionState,
                    canDeleteFiles = true,
                    onClick = {},
                    onCheckChange = {},
                    onMoreClick = {},
                    onDelete = {}
                )
            }
        }
    }
}
