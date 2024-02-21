package com.flipperdevices.faphub.installation.manifest.model

import kotlinx.collections.immutable.ImmutableList

sealed class FapManifestState {
    data object Loading : FapManifestState()

    class NotLoaded(val throwable: Throwable) : FapManifestState()

    data class LoadedOffline(
        val items: ImmutableList<FapManifestItem>
    ) : FapManifestState()

    data class Loaded(
        val items: ImmutableList<FapManifestEnrichedItem>
    ) : FapManifestState()
}
