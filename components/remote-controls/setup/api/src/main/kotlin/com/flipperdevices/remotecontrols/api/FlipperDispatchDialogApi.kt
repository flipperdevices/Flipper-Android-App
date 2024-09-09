package com.flipperdevices.remotecontrols.api

import androidx.compose.runtime.Composable
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface FlipperDispatchDialogApi {
    enum class DialogType {
        FLIPPER_IS_BUSY, FLIPPER_NOT_CONNECTED, FLIPPER_NOT_SUPPORTED
    }

    @Composable
    fun Render(
        dialogType: FlipperDispatchDialogApi.DialogType?,
        onDismiss: () -> Unit
    )

    fun interface Factory {
        operator fun invoke(
            onBack: DecomposeOnBackParameter
        ): FlipperDispatchDialogApi
    }

    companion object {
        fun DispatchSignalApi.State.toDialogType() = when (this) {
            DispatchSignalApi.State.FlipperIsBusy -> DialogType.FLIPPER_IS_BUSY
            DispatchSignalApi.State.FlipperNotConnected -> DialogType.FLIPPER_NOT_CONNECTED
            DispatchSignalApi.State.FlipperNotSupported -> DialogType.FLIPPER_NOT_SUPPORTED
            else -> null
        }
    }
}
