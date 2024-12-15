package com.flipperdevices.filemanager.transfer.impl.composable

import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.filemanager.transfer.impl.viewmodel.OptionsViewModel.Action
import com.flipperdevices.filemanager.ui.components.dropdown.IconDropdownItem
import com.flipperdevices.filemanager.ui.components.dropdown.RadioDropdownItem
import flipperapp.components.filemngr.transfer.impl.generated.resources.fmt_otp_create_folder
import flipperapp.components.filemngr.transfer.impl.generated.resources.fmt_otp_display_grid
import flipperapp.components.filemngr.transfer.impl.generated.resources.fmt_otp_display_list
import flipperapp.components.filemngr.transfer.impl.generated.resources.fmt_otp_show_hidden
import flipperapp.components.filemngr.transfer.impl.generated.resources.fmt_otp_sort_default
import flipperapp.components.filemngr.transfer.impl.generated.resources.fmt_otp_sort_size
import flipperapp.components.filemngr.ui_components.generated.resources.ic_create_fodler
import flipperapp.components.filemngr.ui_components.generated.resources.ic_grid
import flipperapp.components.filemngr.ui_components.generated.resources.ic_list
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sort_default
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sort_size
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.transfer.impl.generated.resources.Res as FMT
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
fun ListOptionsDropDown(
    isVisible: Boolean,
    isHiddenFilesVisible: Boolean,
    onAction: (Action) -> Unit,
    onCreateFolderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isVisible,
        onDismissRequest = { onAction.invoke(Action.ToggleMenu) },
    ) {
        IconDropdownItem(
            text = stringResource(FMT.string.fmt_otp_create_folder),
            painter = painterResource(FR.drawable.ic_create_fodler),
            onClick = onCreateFolderClick
        )
        Divider()
        IconDropdownItem(
            text = stringResource(FMT.string.fmt_otp_display_list),
            painter = painterResource(FR.drawable.ic_list),
            onClick = { onAction.invoke(Action.DisplayList) }
        )
        IconDropdownItem(
            text = stringResource(FMT.string.fmt_otp_display_grid),
            painter = painterResource(FR.drawable.ic_grid),
            onClick = { onAction.invoke(Action.DisplayGrid) }
        )
        Divider()
        IconDropdownItem(
            text = stringResource(FMT.string.fmt_otp_sort_default),
            painter = painterResource(FR.drawable.ic_sort_default),
            onClick = { onAction.invoke(Action.SortByDefault) }
        )
        IconDropdownItem(
            text = stringResource(FMT.string.fmt_otp_sort_size),
            painter = painterResource(FR.drawable.ic_sort_size),
            onClick = { onAction.invoke(Action.SortBySize) }
        )
        Divider()
        RadioDropdownItem(
            text = stringResource(FMT.string.fmt_otp_show_hidden),
            onClick = { onAction.invoke(Action.ToggleHidden) },
            selected = isHiddenFilesVisible
        )
    }
}
