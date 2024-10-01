package com.flipperdevices.filemanager.listing.impl.composable.options

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.ui.components.R as FR

@Composable
fun ListOptionsDropDown(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    isHiddenFilesVisible: Boolean,
    onSelectClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onCreateFileClick: () -> Unit,
    onUploadClick: () -> Unit,
    onListClick: () -> Unit,
    onGridClick: () -> Unit,
    onSortByDefaultClick: () -> Unit,
    onSortBySizeClick: () -> Unit,
    onShowHiddenFilesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isVisible,
        onDismissRequest = onDismiss,
    ) {
        IconDropdownItem(
            text = "Select",
            painter = painterResource(FR.drawable.ic_select),
            onClick = onSelectClick
        )
        IconDropdownItem(
            text = "Create Folder",
            painter = painterResource(FR.drawable.ic_create_fodler),
            onClick = onCreateFolderClick
        )

        IconDropdownItem(
            text = "Create File",
            painter = painterResource(FR.drawable.ic_create_file),
            onClick = onCreateFileClick
        )
        IconDropdownItem(
            text = "Upload",
            painter = painterResource(FR.drawable.ic_upload),
            onClick = onUploadClick
        )
        Divider()
        IconDropdownItem(
            text = "List",
            painter = painterResource(FR.drawable.ic_list),
            onClick = onListClick
        )
        IconDropdownItem(
            text = "Grid",
            painter = painterResource(FR.drawable.ic_grid),
            onClick = onGridClick
        )
        Divider()
        IconDropdownItem(
            text = "Sort by Default",
            painter = painterResource(FR.drawable.ic_sort_default),
            onClick = onSortByDefaultClick
        )
        IconDropdownItem(
            text = "Sort by Size",
            painter = painterResource(FR.drawable.ic_sort_size),
            onClick = onSortBySizeClick
        )
        Divider()
        RadioDropdownItem(
            text = "Show Hidden Files",
            onClick = onShowHiddenFilesClick,
            selected = isHiddenFilesVisible
        )
    }
}

@Composable
private fun FDropdownItem(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    colorText: Color = LocalPallet.current.text100,
) {
    DropdownMenuItem(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        onClick = {
            if (isActive) {
                onClick()
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon.invoke()
            Text(
                text = text,
                style = LocalTypography.current.bodyM14,
                color = if (isActive) {
                    colorText
                } else {
                    LocalPallet.current.keyScreenDisabled
                }
            )
        }
    }
}

@Composable
private fun IconDropdownItem(
    text: String,
    painter: Painter,
    isEnabled: Boolean = true,
    colorText: Color = LocalPallet.current.text100,
    colorIcon: Color = LocalPallet.current.text100,
    onClick: () -> Unit,
) {
    FDropdownItem(
        onClick = onClick,
        colorText = colorText,
        text = text,
        isActive = isEnabled,
        icon = {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painter,
                tint = animateColorAsState(
                    if (isEnabled) {
                        colorIcon
                    } else {
                        LocalPallet.current.keyScreenDisabled
                    }
                ).value,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun RadioDropdownItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    colorText: Color = LocalPallet.current.text100,
) {
    FDropdownItem(
        modifier = modifier,
        onClick = onClick,
        colorText = colorText,
        text = text,
        isActive = isEnabled,
        icon = {
            RadioButton(
                modifier = Modifier.size(20.dp),
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = LocalPalletV2.current.action.brand.background.primary.default,
                    disabledColor = LocalPalletV2.current.action.brand.background.primary.disabled,
                )
            )
        }
    )
}
