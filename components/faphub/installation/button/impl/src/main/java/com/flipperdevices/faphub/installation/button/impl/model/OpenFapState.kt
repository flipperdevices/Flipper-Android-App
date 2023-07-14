package com.flipperdevices.faphub.installation.button.impl.model

sealed class OpenFapState {
    object NotSupported : OpenFapState()
    object Ready : OpenFapState()
    data class InProgress(val appId: String) : OpenFapState()
}
