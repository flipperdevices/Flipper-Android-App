package com.flipperdevices.remotecontrols.impl.setup.api.dialog.composable

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.busy.ComposableFlipperBusy
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi.DialogType
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig

@Composable
fun SetupFlipperDialogComposable(
    flipperDialog: DialogType?,
    onDismiss: () -> Unit,
    openDeviceTab: () -> Unit
) {
    val rootNavigation = LocalRootNavigation.current
    when (flipperDialog) {
        DialogType.FLIPPER_IS_BUSY -> {
            ComposableFlipperBusy(
                onDismiss = onDismiss,
                goToRemote = {
                    onDismiss.invoke()
                    rootNavigation.push(RootScreenConfig.ScreenStreaming)
                }
            )
        }

        DialogType.FLIPPER_NOT_CONNECTED -> {
            ComposableFlipperNotConnectedDialog(
                onDismiss = onDismiss,
                onOpenDeviceTab = openDeviceTab
            )
        }

        DialogType.FLIPPER_NOT_SUPPORTED -> {
            ComposableFlipperNotSupportedDialog(
                onDismiss = onDismiss,
                onOpenDeviceTab = openDeviceTab
            )
        }

        null -> Unit
    }
}
