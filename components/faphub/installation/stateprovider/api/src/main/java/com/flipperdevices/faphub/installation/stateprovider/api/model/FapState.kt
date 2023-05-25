package com.flipperdevices.faphub.installation.stateprovider.api.model

import androidx.annotation.FloatRange

sealed class FapState {
    object NotInitialized : FapState()

    object RetrievingManifest : FapState()

    object Installed : FapState()

    object ReadyToInstall : FapState()

    object ReadyToUpdate : FapState()

    data class InstallationInProgress(
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float
    ) : FapState()

    data class UpdatingInProgress(
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float
    ) : FapState()
}