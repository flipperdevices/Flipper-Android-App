package com.flipperdevices.faphub.installation.manifest.impl.model

import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import kotlinx.collections.immutable.ImmutableList

sealed class FapManifestLoaderState {

    data class Failed(val throwable: Throwable) : FapManifestLoaderState()

    data class Loaded(
        val items: ImmutableList<FapManifestItem>,
        val isLoading: Boolean
    ) : FapManifestLoaderState()

    fun toManifestState() = when (this) {
        is Failed -> FapManifestState.NotLoaded(throwable)
        is Loaded -> FapManifestState.Loaded(
            items = items,
            inProgress = isLoading
        )
    }
}
