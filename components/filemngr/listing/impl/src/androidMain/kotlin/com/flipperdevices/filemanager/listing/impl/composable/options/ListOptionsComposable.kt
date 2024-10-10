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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel.Action
import com.flipperdevices.filemanager.listing.impl.R as FML
import com.flipperdevices.filemanager.ui.components.R as FR

@Composable
fun ListOptionsDropDown(
    isVisible: Boolean,
    canCreateFiles: Boolean,
    isHiddenFilesVisible: Boolean,
    onAction: (Action) -> Unit,
    onSelectClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onCreateFileClick: () -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isVisible,
        onDismissRequest = { onAction.invoke(Action.ToggleMenu) },
    ) {
        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_select),
            painter = painterResource(FR.drawable.ic_select),
            onClick = onSelectClick
        )
        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_create_folder),
            painter = painterResource(FR.drawable.ic_create_fodler),
            onClick = onCreateFolderClick
        )

        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_create_file),
            painter = painterResource(FR.drawable.ic_create_file),
            onClick = onCreateFileClick,
            isActive = canCreateFiles
        )
        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_upload),
            painter = painterResource(FR.drawable.ic_upload),
            onClick = onUploadClick,
        )
        Divider()
        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_display_list),
            painter = painterResource(FR.drawable.ic_list),
            onClick = { onAction.invoke(Action.DisplayList) }
        )
        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_display_grid),
            painter = painterResource(FR.drawable.ic_grid),
            onClick = { onAction.invoke(Action.DisplayGrid) }
        )
        Divider()
        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_sort_default),
            painter = painterResource(FR.drawable.ic_sort_default),
            onClick = { onAction.invoke(Action.SortByDefault) }
        )
        IconDropdownItem(
            text = stringResource(FML.string.fml_otp_sort_size),
            painter = painterResource(FR.drawable.ic_sort_size),
            onClick = { onAction.invoke(Action.SortBySize) }
        )
        Divider()
        RadioDropdownItem(
            text = stringResource(FML.string.fml_otp_show_hidden),
            onClick = { onAction.invoke(Action.ToggleHidden) },
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
    colorText: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
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
                    LocalPalletV2.current.action.blackAndWhite.text.disabled
                }
            )
        }
    }
}

@Composable
fun IconDropdownItem(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    colorText: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
    colorIcon: Color = LocalPalletV2.current.action.blackAndWhite.icon.default,
) {
    FDropdownItem(
        modifier = modifier,
        onClick = onClick,
        colorText = animateColorAsState(
            if (isActive) {
                colorText
            } else {
                LocalPalletV2.current.action.blackAndWhite.text.disabled
            }
        ).value,
        text = text,
        isActive = isActive,
        icon = {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painter,
                tint = animateColorAsState(
                    if (isActive) {
                        colorIcon
                    } else {
                        LocalPalletV2.current.action.blackAndWhite.icon.disabled
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
    colorText: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
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
