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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.ui.components.itemcard.components.ItemCardEndBox
import com.flipperdevices.filemanager.ui.components.itemcard.components.ItemCardSubtitle
import com.flipperdevices.filemanager.ui.components.itemcard.components.ItemCardTitle
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import flipperapp.components.filemngr.ui_components.generated.resources.ic_trash_white
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
fun SwipeToDismissFolderCardListComposable(
    painter: Painter,
    title: String,
    subtitle: String,
    selectionState: ItemUiSelectionState,
    canDeleteFiles: Boolean,
    onClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    onMoreClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
) {
    val scope = rememberCoroutineScope()
    val revealState = rememberRevealState()
    SwipeToReveal(
        revealState = revealState,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LocalPalletV2.current.action.danger.background.tertiary.default),
        content = {
            FolderCardListComposable(
                painter = painter,
                title = title,
                subtitle = subtitle,
                selectionState = selectionState,
                onCheckChange = onCheckChange,
                onMoreClick = onMoreClick,
                onClick = onClick,
                iconTint = iconTint,
            )
        },
        actions = {
            if (canDeleteFiles) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .clip(
                            RoundedCornerShape(
                                topEnd = 12.dp,
                                bottomEnd = 12.dp
                            )
                        )
                        .background(LocalPalletV2.current.action.danger.background.tertiary.default)
                        .clickableRipple {
                            scope.launch {
                                revealState.animateHide()
                                onDelete.invoke()
                            }
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        painter = painterResource(FR.drawable.ic_trash_white),
                        tint = LocalPalletV2.current.action.danger.icon.default,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
    showEndBox: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickableRipple(onClick = onClick)
            .background(LocalPalletV2.current.surface.contentCard.body.default)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(weight = 1f, fill = false)
        ) {
            Icon(
                painter = painter,
                tint = iconTint,
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

        if (showEndBox) {
            ItemCardEndBox(
                selectionState = selectionState,
                onCheckChange = onCheckChange,
                onMoreClick = onMoreClick
            )
        }
    }
}
