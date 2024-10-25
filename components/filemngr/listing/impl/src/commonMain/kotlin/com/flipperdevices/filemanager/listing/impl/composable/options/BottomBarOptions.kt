package com.flipperdevices.filemanager.listing.impl.composable.options

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.listing.impl.model.PathWithType
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.EditFileViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.FilesViewModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.SelectionViewModel
import com.flipperdevices.filemanager.ui.components.dropdown.IconDropdownItem
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_copy_to
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_dialog_delete_btn
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_export
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_more
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_move
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_rename
import flipperapp.components.filemngr.ui_components.generated.resources.ic_copy_to
import flipperapp.components.filemngr.ui_components.generated.resources.ic_edit
import flipperapp.components.filemngr.ui_components.generated.resources.ic_more_points_white
import flipperapp.components.filemngr.ui_components.generated.resources.ic_move
import flipperapp.components.filemngr.ui_components.generated.resources.ic_trash_white
import flipperapp.components.filemngr.ui_components.generated.resources.ic_upload
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.listing.impl.generated.resources.Res as FML
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
private fun MoreBottomBarOptions(
    onCopyTo: () -> Unit,
    canRename: Boolean,
    onRename: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        VerticalTextIconButton(
            text = stringResource(FML.string.fml_more),
            painter = painterResource(FR.drawable.ic_more_points_white),
            onClick = { isExpanded = !isExpanded }
        )
        DropdownMenu(
            modifier = Modifier,
            expanded = isExpanded,
            onDismissRequest = { isExpanded = !isExpanded },
        ) {
            IconDropdownItem(
                text = stringResource(FML.string.fml_rename),
                painter = painterResource(FR.drawable.ic_edit),
                onClick = onRename,
                isActive = canRename
            )
            IconDropdownItem(
                text = stringResource(FML.string.fml_copy_to),
                painter = painterResource(FR.drawable.ic_copy_to),
                onClick = onCopyTo
            )
        }
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
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
            text = stringResource(FML.string.fml_dialog_delete_btn),
            painter = painterResource(FR.drawable.ic_trash_white),
            iconTint = LocalPalletV2.current.action.danger.icon.default,
            textColor = LocalPalletV2.current.action.danger.text.default,
            onClick = onDelete
        )

        VerticalTextIconButton(
            text = stringResource(FML.string.fml_move),
            painter = painterResource(FR.drawable.ic_move),
            onClick = onMove
        )

        VerticalTextIconButton(
            text = stringResource(FML.string.fml_export),
            painter = painterResource(FR.drawable.ic_upload),
            onClick = onExport
        )
        MoreBottomBarOptions(
            onCopyTo = onCopyTo,
            canRename = canRename,
            onRename = onRename,
        )
    }
}

@Composable
fun FullScreenBottomBarOptions(
    deleteFileViewModel: DeleteFilesViewModel,
    editFileViewModel: EditFileViewModel,
    selectionViewModel: SelectionViewModel,
    filesListState: FilesViewModel.State,
    selectionState: SelectionViewModel.State,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            selectionState.isEnabled && filesListState is FilesViewModel.State.Loaded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            BottomBarOptions(
                canRename = selectionState.canRename,
                onMove = {}, // todo
                onRename = {
                    val path = selectionState.selected.firstOrNull() ?: return@BottomBarOptions
                    selectionViewModel.toggleMode()
                    editFileViewModel.onRename(path)
                },
                onDelete = {
                    deleteFileViewModel.tryDelete(selectionState.selected.map(PathWithType::fullPath))
                    selectionViewModel.toggleMode()
                },
                onExport = {}, // todo
                onCopyTo = {} // todo
            )
        }
    }
}
