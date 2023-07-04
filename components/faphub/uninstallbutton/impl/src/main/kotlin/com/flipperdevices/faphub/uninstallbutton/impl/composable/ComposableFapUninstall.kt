package com.flipperdevices.faphub.uninstallbutton.impl.composable

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.uninstallbutton.impl.R
import com.flipperdevices.faphub.uninstallbutton.impl.viewmodel.DeleteViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableFapUninstall(
    applicationUid: String,
    dialogAppBox: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Icon(
        modifier = modifier
            .clickableRipple(
                onClick = {
                    showDeleteDialog = true
                }
            ),
        painter = painterResource(R.drawable.ic_delete),
        contentDescription = stringResource(R.string.faphub_delete_desc),
        tint = LocalPallet.current.onError
    )

    if (showDeleteDialog) {
        val deleteViewModel = tangleViewModel<DeleteViewModel>()
        ComposableDeleteConfirmDialog(
            dialogAppBox = dialogAppBox,
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = { deleteViewModel.onDelete(applicationUid) }
        )
    }
}
