package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.flipperdevices.core.preference.pb.FileManagerOrientation
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState

@Composable
fun FolderCardComposable(
    painter: Painter,
    title: String,
    subtitle: String,
    isSubtitleLoading: Boolean,
    selectionState: ItemUiSelectionState,
    canDeleteFiles: Boolean,
    onClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    onMoreClick: () -> Unit,
    onDelete: () -> Unit,
    orientation: FileManagerOrientation,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
) {
    when (orientation) {
        FileManagerOrientation.GRID -> {
            FolderCardGridComposable(
                painter = painter,
                title = title,
                subtitle = subtitle,
                isSubtitleLoading = isSubtitleLoading,
                selectionState = selectionState,
                onClick = onClick,
                onCheckChange = onCheckChange,
                onMoreClick = onMoreClick,
                modifier = modifier,
                iconTint = iconTint
            )
        }

        FileManagerOrientation.LIST,
        is FileManagerOrientation.Unrecognized -> {
            SwipeToDismissFolderCardListComposable(
                painter = painter,
                title = title,
                subtitle = subtitle,
                isSubtitleLoading = isSubtitleLoading,
                selectionState = selectionState,
                onClick = onClick,
                onCheckChange = onCheckChange,
                onMoreClick = onMoreClick,
                modifier = modifier,
                iconTint = iconTint,
                onDelete = onDelete,
                canDeleteFiles = canDeleteFiles,
            )
        }
    }
}
