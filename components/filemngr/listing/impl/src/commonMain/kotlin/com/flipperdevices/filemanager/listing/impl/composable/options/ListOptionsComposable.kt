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
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.listing.impl.viewmodel.OptionsViewModel.Action
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_create_file
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_create_folder
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_display_grid
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_display_list
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_select
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_show_hidden
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_sort_default
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_sort_size
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_otp_upload
import flipperapp.components.filemngr.ui_components.generated.resources.ic_create_file
import flipperapp.components.filemngr.ui_components.generated.resources.ic_create_fodler
import flipperapp.components.filemngr.ui_components.generated.resources.ic_grid
import flipperapp.components.filemngr.ui_components.generated.resources.ic_list
import flipperapp.components.filemngr.ui_components.generated.resources.ic_select
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sort_default
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sort_size
import flipperapp.components.filemngr.ui_components.generated.resources.ic_upload
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.listing.impl.generated.resources.Res as FML
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR
import com.flipperdevices.filemanager.ui.components.dropdown.IconDropdownItem
import com.flipperdevices.filemanager.ui.components.dropdown.RadioDropdownItem

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


