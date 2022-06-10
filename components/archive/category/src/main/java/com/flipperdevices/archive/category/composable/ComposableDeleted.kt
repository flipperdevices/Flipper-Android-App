package com.flipperdevices.archive.category.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.category.R
import com.flipperdevices.archive.category.viewmodels.DeleteViewModel
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.archive.shared.composable.ComposableAppBar
import com.flipperdevices.core.ui.ktx.LocalRouter

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

    ComposableAppBar(
        title = stringResource(R.string.category_deleted_title),
        onBack = { router.exit() },
        endContent = {
            var showMenu by remember { mutableStateOf(false) }

            Row(
                modifier = it,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = { showMenu = true }
                    ),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(onClick = {
                        deleteViewModel.onDeleteAll()
                        showMenu = false
                    }) {
                        Text(text = stringResource(R.string.category_item_delete_all))
                    }
                    DropdownMenuItem(onClick = {
                        deleteViewModel.onRestoreAll()
                        showMenu = false
                    }) {
                        Text(text = stringResource(R.string.category_item_restore_all))
                    }
                }
            }
        }
    )
}
