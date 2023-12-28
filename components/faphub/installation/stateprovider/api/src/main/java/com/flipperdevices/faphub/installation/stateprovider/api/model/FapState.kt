package com.flipperdevices.faphub.installation.stateprovider.api.model

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

@Immutable
sealed class FapState {
    data object NotInitialized : FapState()

    data object RetrievingManifest : FapState()

    data object Installed : FapState()

    data object ReadyToInstall : FapState()

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

    data object Deleting : FapState()

    data object Canceling : FapState()
}

enum class NotAvailableReason {
    BUILD_RUNNING,
    UNSUPPORTED_APP,
    FLIPPER_OUTDATED,
    UNSUPPORTED_SDK,
    NO_SD_CARD,
    FLIPPER_NOT_CONNECTED
}
