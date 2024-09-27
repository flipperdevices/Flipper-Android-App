package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.ui.components.itemcard.components.ItemCardEndBox
import com.flipperdevices.filemanager.ui.components.itemcard.components.ItemCardSubtitle
import com.flipperdevices.filemanager.ui.components.itemcard.components.ItemCardTitle
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import kotlinx.coroutines.launch
import com.flipperdevices.filemanager.ui.components.R as FR

@Composable
fun SwipeToDismissFolderCardListComposable(
    painter: Painter,
    title: String,
    subtitle: String,
    selectionState: ItemUiSelectionState,
    onClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    onMoreClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val revealState = rememberRevealState()
    SwipeToReveal(
        revealState = revealState,
        modifier = modifier,
        content = {
            FolderCardListComposable(
                painter = painter,
                title = title,
                subtitle = subtitle,
                selectionState = selectionState,
                onCheckChange = onCheckChange,
                onMoreClick = onMoreClick,
                onClick = onClick,
                modifier = cardModifier
            )
        },

        actions = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .background(LocalPalletV2.current.action.danger.background.tertiary.default)
                    .padding(12.dp)
                    .clickableRipple {
                        scope.launch {
                            revealState.animateHide()
                            onDelete.invoke()
                        }
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(FR.drawable.ic_trash_white),
                    tint = LocalPalletV2.current.action.danger.icon.default,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    )
}

@Composable
fun FolderCardListComposable(
    painter: Painter,
    title: String,
    subtitle: String,
    selectionState: ItemUiSelectionState,
    onClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickableRipple(onClick = onClick)
            .background(LocalPalletV2.current.surface.contentCard.body.default)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(weight = 1f, fill = false)
        ) {
            Icon(
                painter = painter,
                tint = LocalPalletV2.current.icon.blackAndWhite.default,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                ItemCardTitle(title)
                ItemCardSubtitle(subtitle)
            }
        }

        ItemCardEndBox(
            selectionState = selectionState,
            onCheckChange = onCheckChange,
            onMoreClick = onMoreClick
        )
    }
}

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
                    onClick = {},
                    onCheckChange = {},
                    onMoreClick = {},
                    onDelete = {}
                )
            }
        }
    }
}
