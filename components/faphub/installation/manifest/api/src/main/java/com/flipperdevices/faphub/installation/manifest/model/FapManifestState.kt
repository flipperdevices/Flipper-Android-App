package com.flipperdevices.faphub.installation.manifest.model

import kotlinx.collections.immutable.ImmutableList

sealed class FapManifestState {
    class NotLoaded(val throwable: Throwable) : FapManifestState()

    data class Loaded(
        val items: ImmutableList<FapManifestItem>,
        val inProgress: Boolean
    ) : FapManifestState()
}
