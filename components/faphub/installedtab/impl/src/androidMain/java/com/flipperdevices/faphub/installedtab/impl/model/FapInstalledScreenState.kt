package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItemShort
import kotlinx.collections.immutable.ImmutableList

sealed class FapInstalledScreenState {
    object Loading : FapInstalledScreenState()

    data class Loaded(
        val faps: ImmutableList<Pair<FapItemShort, FapInstalledInternalState>>
    ) : FapInstalledScreenState()

    data class LoadedOffline(
        val faps: ImmutableList<OfflineFapApp>
    ) : FapInstalledScreenState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledScreenState()
}
