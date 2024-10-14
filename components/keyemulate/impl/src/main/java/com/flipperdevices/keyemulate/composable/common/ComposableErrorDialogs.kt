package com.flipperdevices.keyemulate.composable.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.dialog.composable.busy.ComposableFlipperBusy
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.impl.R
import com.flipperdevices.keyemulate.model.EmulateButtonState

@Composable
fun ComposableErrorDialogs(
    state: EmulateButtonState,
    onCloseDialog: () -> Unit,
    onOpenRemote: () -> Unit,
) {
    if (state == EmulateButtonState.AppAlreadyOpenDialog) {
        ComposableFlipperBusy(onCloseDialog, onOpenRemote)
    } else if (state == EmulateButtonState.ForbiddenFrequencyDialog) {
        ComposableForbiddenFrequencyDialog(onCloseDialog)
    }
}

@Composable
private fun ComposableForbiddenFrequencyDialog(onCloseDialog: () -> Unit) {
    FlipperDialog(
        title = stringResource(R.string.forbidden_dialog_title),
        text = stringResource(R.string.forbidden_dialog_desc),
        buttonText = stringResource(R.string.forbidden_dialog_btn),
        onClickButton = onCloseDialog,
        onDismissRequest = onCloseDialog,
        imageComposable = {
            Icon(
                modifier = Modifier.size(width = 120.dp, height = 112.dp),
                painter = painterResource(R.drawable.pic_flipper_alert),
                contentDescription = stringResource(R.string.forbidden_dialog_title),
                tint = LocalPallet.current.text100
            )
        }
    )
}
