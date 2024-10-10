package com.flipperdevices.filemanager.ui.components.itemcard.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.ui.components.itemcard.model.ItemUiSelectionState
import com.flipperdevices.filemanager.ui.components.R as FR

@Composable
internal fun ItemCardEndBox(
    selectionState: ItemUiSelectionState,
    onCheckChange: (Boolean) -> Unit,
    onMoreClick: () -> Unit
) {
    AnimatedContent(
        targetState = selectionState,
        contentKey = {
            when (it) {
                ItemUiSelectionState.NONE -> it
                ItemUiSelectionState.SELECTED -> 0
                ItemUiSelectionState.UNSELECTED -> 1
            }
        }
    ) { selectionStateAnimated ->

        when (selectionStateAnimated) {
            ItemUiSelectionState.NONE -> {
                Icon(
                    painter = painterResource(FR.drawable.ic_more_points_white),
                    tint = LocalPalletV2.current.action.neutral.icon.tertiary.default,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickableRipple(onClick = onMoreClick)
                )
            }

            ItemUiSelectionState.UNSELECTED,
            ItemUiSelectionState.SELECTED -> {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = LocalPalletV2.current.action.neutral.border.tertiary.default,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Checkbox(
                        checked = selectionStateAnimated == ItemUiSelectionState.SELECTED,
                        onCheckedChange = onCheckChange,
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = LocalPalletV2.current.action.brand.icon.default,
                            checkedColor = Color.Transparent,
                            uncheckedColor = Color.Transparent,
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
