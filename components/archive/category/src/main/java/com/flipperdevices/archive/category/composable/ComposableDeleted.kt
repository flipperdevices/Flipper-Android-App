package com.flipperdevices.archive.category.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.category.R
import com.flipperdevices.archive.category.composable.dialogs.ComposableDeleteAllDialog
import com.flipperdevices.archive.category.composable.dialogs.ComposableRestoreAllDialog
import com.flipperdevices.archive.category.viewmodels.DeleteViewModel
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.archive.shared.composable.ComposableAppBar
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposableDeleted() {
    Column {
        ComposableDeletedAppBar()
        ComposableCategoryContent(CategoryType.Deleted, synchronizationUiApi = null)
    }
}

@Composable
private fun ComposableDeletedAppBar(
    deleteViewModel: DeleteViewModel = viewModel()
) {
    val router = LocalRouter.current
    var isDeleteAllDialog by remember { mutableStateOf(false) }
    if (isDeleteAllDialog) {
        ComposableDeleteAllDialog(
            onAction = {
                isDeleteAllDialog = false
                deleteViewModel.onDeleteAll()
            },
            onCancel = { isDeleteAllDialog = false }
        )
    }

    var isRestoreAllDialog by remember { mutableStateOf(false) }
    if (isRestoreAllDialog) {
        ComposableRestoreAllDialog(
            onAction = {
                isRestoreAllDialog = false
                deleteViewModel.onRestoreAll()
            },
            onCancel = { isRestoreAllDialog = false }
        )
    }

    ComposableAppBar(
        title = stringResource(R.string.category_deleted_title),
        onBack = { router.exit() },
        endContent = {
            ComposableDeletedAppBarInternal(
                modifier = it,
                onDeleteAllDialogOpen = { isDeleteAllDialog = true },
                onRestoreAllDialogOpen = { isRestoreAllDialog = true }
            )
        }
    )
}

@Composable
private fun ComposableDeletedAppBarInternal(
    modifier: Modifier,
    onDeleteAllDialogOpen: () -> Unit,
    onRestoreAllDialogOpen: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            modifier = Modifier.clickableRipple { showMenu = true },
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = LocalPallet.current.onAppBar
        )
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(onClick = {
                onDeleteAllDialogOpen()
                showMenu = false
            }) {
                Text(text = stringResource(R.string.category_item_delete_all))
            }
            DropdownMenuItem(onClick = {
                onRestoreAllDialogOpen()
                showMenu = false
            }) {
                Text(text = stringResource(R.string.category_item_restore_all))
            }
        }
    }
}
