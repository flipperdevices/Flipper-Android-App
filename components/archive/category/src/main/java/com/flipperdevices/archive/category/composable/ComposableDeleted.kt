package com.flipperdevices.archive.category.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.flipperdevices.archive.category.R
import com.flipperdevices.archive.category.composable.dialogs.ComposableDeleteAllDialog
import com.flipperdevices.archive.category.composable.dialogs.ComposableRestoreAllDialog
import com.flipperdevices.archive.category.viewmodels.DeleteViewModel
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableDeleted(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    Column(modifier = modifier) {
        ComposableDeletedAppBar(onBack = onBack)
        ComposableCategoryContent(
            categoryType = CategoryType.Deleted,
            synchronizationUiApi = null,
            onOpenKeyScreen = onOpenKeyScreen
        )
    }
}

@Composable
private fun ComposableDeletedAppBar(
    deleteViewModel: DeleteViewModel = tangleViewModel(),
    onBack: () -> Unit
) {
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

    OrangeAppBar(
        title = stringResource(R.string.category_deleted_title),
        onBack = onBack,
        endBlock = {
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
    onDeleteAllDialogOpen: () -> Unit,
    modifier: Modifier = Modifier,
    onRestoreAllDialogOpen: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            modifier = Modifier.clickableRipple(bounded = false) { showMenu = true },
            painter = painterResource(DesignSystem.drawable.ic_more_points),
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
