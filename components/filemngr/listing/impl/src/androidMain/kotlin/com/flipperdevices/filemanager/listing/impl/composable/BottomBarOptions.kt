package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.listing.impl.composable.options.IconDropdownItem
import com.flipperdevices.filemanager.ui.components.R as FR

@Composable
private fun VerticalTextIconButton(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = LocalPalletV2.current.icon.blackAndWhite.default,
    textColor: Color = LocalPalletV2.current.action.blackAndWhite.text.default
) {
    Column(
        modifier = modifier
            .clickableRipple(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painter,
            tint = iconTint,
            modifier = Modifier.size(24.dp),
            contentDescription = null
        )
        Text(
            text = text,
            style = LocalTypography.current.subtitleM12,
            color = textColor
        )
    }
}

@Composable
fun BottomBarOptions(
    canRename: Boolean,
    onRename: () -> Unit,
    onExport: () -> Unit,
    onMove: () -> Unit,
    onCopyTo: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = LocalPalletV2.current.surface.border.default.secondary,
                shape = RoundedCornerShape(12.dp)
            )
            .background(LocalPalletV2.current.surface.border.default.secondary)
            .padding(1.dp)
            .background(LocalPalletV2.current.surface.popUp.body.default),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalTextIconButton(
            text = "Delete",
            painter = painterResource(FR.drawable.ic_trash_white),
            iconTint = LocalPalletV2.current.action.danger.icon.default,
            textColor = LocalPalletV2.current.action.danger.text.default,
            onClick = onDelete
        )

        VerticalTextIconButton(
            text = "Move",
            painter = painterResource(FR.drawable.ic_move),
            onClick = onMove
        )

        VerticalTextIconButton(
            text = "Export",
            painter = painterResource(FR.drawable.ic_upload),
            onClick = onExport
        )
        var isExpanded by remember { mutableStateOf(false) }

        Box {
            VerticalTextIconButton(
                text = "More",
                painter = painterResource(FR.drawable.ic_more_points_white),
                onClick = { isExpanded = !isExpanded }
            )
            DropdownMenu(
                modifier = Modifier,
                expanded = isExpanded,
                onDismissRequest = { isExpanded = !isExpanded },
            ) {
                IconDropdownItem(
                    text = "Rename",
                    painter = painterResource(FR.drawable.ic_edit),
                    onClick = onRename
                )
                IconDropdownItem(
                    text = "Copy to",
                    painter = painterResource(FR.drawable.ic_copy_to),
                    onClick = onCopyTo
                )
            }
        }
    }
}

@Preview
@Composable
private fun BottomBarOptionsPreview() {
    FlipperThemeInternal {
        BottomBarOptions(
            onDelete = {},
            onExport = {},
            onRename = {},
            onCopyTo = {},
            onMove = {},
            canRename = true
        )
    }
}
