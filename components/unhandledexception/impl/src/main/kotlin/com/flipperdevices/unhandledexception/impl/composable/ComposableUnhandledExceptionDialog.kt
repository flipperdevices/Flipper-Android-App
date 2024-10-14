package com.flipperdevices.unhandledexception.impl.composable

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.unhandledexception.impl.R

@Composable
fun ComposableUnhandledExceptionDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val dialogModel = remember(context) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.unhandledexception_dialog_title)
            .setImage {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(R.drawable.pic_dialog_reboot_bluetooth),
                    contentDescription = stringResource(R.string.unhandledexception_dialog_title)
                )
            }
            .setDescription(content = { ComposableUnhandledExceptionDialogText() })
            .addButton(
                R.string.unhandledexception_dialog_btn_go_to_settings,
                {
                    val intent = Intent(
                        Settings.ACTION_BLUETOOTH_SETTINGS
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
                isActive = true
            )
            .addButton(
                R.string.unhandledexception_dialog_btn_close,
                onClick = onDismiss
            )
            .setOnDismissRequest(onDismiss)
            .setCloseOnClickOutside(false)
            .build()
    }

    FlipperMultiChoiceDialog(model = dialogModel, modifier = modifier)
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun DescriptionAnnotatedStringPreview() {
    FlipperThemeInternal {
        Box {
            ComposableUnhandledExceptionDialog(
                modifier = Modifier,
                onDismiss = {}
            )
        }
    }
}
