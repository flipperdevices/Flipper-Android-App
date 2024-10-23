package com.flipperdevices.core.ui.dialog.composable.busy

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import flipperapp.components.core.ui.dialog.generated.resources.Res
import flipperapp.components.core.ui.dialog.generated.resources.core_ui_dialog_flipper_busy_action
import flipperapp.components.core.ui.dialog.generated.resources.core_ui_dialog_flipper_busy_desc
import flipperapp.components.core.ui.dialog.generated.resources.core_ui_dialog_flipper_busy_title
import flipperapp.components.core.ui.dialog.generated.resources.pic_flipper_is_busy
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComposableFlipperBusy(
    onDismiss: () -> Unit,
    goToRemote: () -> Unit
) {
    FlipperDialog(
        painter = painterResource(Res.drawable.pic_flipper_is_busy),
        title = stringResource(Res.string.core_ui_dialog_flipper_busy_title),
        text = stringResource(Res.string.core_ui_dialog_flipper_busy_desc),
        buttonText = stringResource(Res.string.core_ui_dialog_flipper_busy_action),
        onDismissRequest = onDismiss,
        onClickButton = {
            onDismiss()
            goToRemote()
        }
    )
}
