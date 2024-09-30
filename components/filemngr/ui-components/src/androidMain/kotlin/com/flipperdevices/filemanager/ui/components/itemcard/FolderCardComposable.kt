package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemCardOrientation
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState

@Composable
fun FolderCardComposable(
    painter: Painter,
    title: String,
    subtitle: String,
    selectionState: ItemUiSelectionState,
    onClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    onMoreClick: () -> Unit,
    onDelete: () -> Unit,
    orientation: ItemCardOrientation,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
) {
    when (orientation) {
        ItemCardOrientation.GRID -> {
            FolderCardGridComposable(
                painter = painter,
                title = title,
                subtitle = subtitle,
                selectionState = selectionState,
                onClick = onClick,
                onCheckChange = onCheckChange,
                onMoreClick = onMoreClick,
                modifier = modifier,
                iconTint = iconTint
            )
        }

        ItemCardOrientation.LIST -> {
            SwipeToDismissFolderCardListComposable(
                painter = painter,
                title = title,
                subtitle = subtitle,
                selectionState = selectionState,
                onClick = onClick,
                onCheckChange = onCheckChange,
                onMoreClick = onMoreClick,
                modifier = modifier,
                iconTint = iconTint,
                onDelete = onDelete
            )
        }
    }
}
