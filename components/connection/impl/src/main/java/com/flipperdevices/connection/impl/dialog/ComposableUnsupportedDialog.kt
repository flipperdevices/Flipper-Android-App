package com.flipperdevices.connection.impl.dialog

import android.content.Intent
import android.net.Uri
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.connection.impl.R
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.core.ui.res.R as DesignSystem

private var inThisSessionAlreadyHided = false

@Composable
fun ComposableUnsupportedDialog(
    supportedState: FlipperSupportedState
) {
    if (supportedState == FlipperSupportedState.READY) {
        return
    }
    var showDialog by remember { mutableStateOf(true) }
    if (inThisSessionAlreadyHided || !showDialog) {
        inThisSessionAlreadyHided = true
        return
    }

    when (supportedState) {
        FlipperSupportedState.DEPRECATED_FLIPPER -> FlipperDialogAndroid(
            imageId = DesignSystem.drawable.ic_firmware_flipper_deprecated,
            titleId = R.string.dialog_unsupported_title,
            textId = R.string.dialog_unsupported_description,
            buttonTextId = R.string.dialog_unsupported_btn,
            onDismissRequest = { showDialog = false },
            onClickButton = { showDialog = false }
        )

        FlipperSupportedState.DEPRECATED_APPLICATION -> {
            val url = stringResource(R.string.dialog_unsupported_application_link)
            val context = LocalContext.current
            FlipperDialogAndroid(
                imageId = if (MaterialTheme.colors.isLight) {
                    DesignSystem.drawable.ic_firmware_application_deprecated
                } else {
                    DesignSystem.drawable.ic_firmware_application_deprecated_dark
                },
                titleId = R.string.dialog_unsupported_application_title,
                textId = R.string.dialog_unsupported_application_description,
                buttonTextId = R.string.dialog_unsupported_application_btn,
                onDismissRequest = { showDialog = false },
                onClickButton = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                    showDialog = false
                }
            )
        }

        FlipperSupportedState.READY -> {} // Do nothing
    }
}
