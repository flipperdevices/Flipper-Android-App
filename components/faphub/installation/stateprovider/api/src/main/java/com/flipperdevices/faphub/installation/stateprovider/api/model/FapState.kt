package com.flipperdevices.faphub.installation.stateprovider.api.model

import androidx.annotation.FloatRange
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

sealed class FapState {
    object NotInitialized : FapState()

    object RetrievingManifest : FapState()

    object Installed : FapState()

    object ReadyToInstall : FapState()
    object ConnectFlipper : FapState()

    data class NotAvailableForInstall(val reason: NotAvailableReason) : FapState()

    data class ReadyToUpdate(val from: FapManifestItem) : FapState()

    data class InstallationInProgress(
        val active: Boolean,
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float
    ) : FapState()

    data class UpdatingInProgress(
        val active: Boolean,
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float
    ) : FapState()

    object Deleting : FapState()

    object Canceling : FapState()
}

enum class NotAvailableReason {
    BUILD_RUNNING,
    UNSUPPORTED_APP,
    FLIPPER_OUTDATED,
    UNSUPPORTED_SDK
}
