package com.flipperdevices.keyemulate.model

import androidx.compose.runtime.Stable

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
    object ForbiddenFrequencyDialog : Inactive()

    @Stable
    data class Active(
        val progress: EmulateProgress,
        val config: EmulateConfig
    ) : EmulateButtonState()
}

enum class DisableButtonReason {
    UNKNOWN,
    UPDATE_FLIPPER,
    NOT_CONNECTED,
    NOT_SYNCHRONIZED
}
