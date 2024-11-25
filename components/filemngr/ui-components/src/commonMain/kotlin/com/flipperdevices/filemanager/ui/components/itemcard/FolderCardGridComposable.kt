package com.flipperdevices.filemanager.ui.components.itemcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
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

@Composable
fun FolderCardGridComposable(
    painter: Painter,
    title: String,
    subtitle: String,
    isSubtitleLoading: Boolean,
    selectionState: ItemUiSelectionState,
    onClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickableRipple(onClick = onClick)
            .background(LocalPalletV2.current.surface.contentCard.body.default)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(weight = 1f, fill = false)
        ) {
            Icon(
                painter = painter,
                tint = iconTint,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(12.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                ItemCardTitle(title)
                ItemCardSubtitle(
                    text = subtitle,
                    isLoading = isSubtitleLoading
                )
            }
        }

        ItemCardEndBox(
            selectionState = selectionState,
            onCheckChange = onCheckChange,
            onMoreClick = onMoreClick
        )
    }
}
