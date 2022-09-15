package com.flipperdevices.keyscreen.emulate.model

import androidx.compose.runtime.Stable
import com.flipperdevices.keyscreen.api.EmulateProgress
import com.flipperdevices.keyscreen.api.LoadingState

@Stable
sealed class EmulateButtonState {
    @Stable
    data class Loading(val state: LoadingState) : EmulateButtonState()

    @Stable
    data class Disabled(val reason: DisableButtonReason) : EmulateButtonState()

    @Stable
    open class Inactive : EmulateButtonState()

    @Stable
    object AppAlreadyOpenDialog : Inactive()

    @Stable
    data class Active(val progress: EmulateProgress) : EmulateButtonState()
}

enum class DisableButtonReason {
    UNKNOWN,
    UPDATE_FLIPPER,
    NOT_CONNECTED,
    NOT_SYNCHRONIZED
}
