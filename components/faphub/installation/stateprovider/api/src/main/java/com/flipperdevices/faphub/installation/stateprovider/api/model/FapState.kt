package com.flipperdevices.faphub.installation.stateprovider.api.model

import androidx.annotation.FloatRange
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

sealed class FapState {
    object NotInitialized : FapState()

    object RetrievingManifest : FapState()

    object Installed : FapState()

    object ReadyToInstall : FapState()

    object FlipperOutdated : FapState()

    object ConnectFlipper : FapState()

    data class ReadyToUpdate(val from: FapManifestItem) : FapState()

    data class InstallationInProgress(
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float
    ) : FapState()

    data class UpdatingInProgress(
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float
    ) : FapState()

    object Deleting : FapState()

    object Canceling : FapState()
}
