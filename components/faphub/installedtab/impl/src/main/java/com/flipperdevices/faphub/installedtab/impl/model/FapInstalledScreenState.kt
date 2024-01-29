package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installedtab.impl.viewmodel.FapInstalledInternalLoadingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

sealed class FapInstalledScreenState {
    data object Loading : FapInstalledScreenState()

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

fun FapInstalledInternalLoadingState.toScreenState() = when (this) {
    is FapInstalledInternalLoadingState.Error -> FapInstalledScreenState.Error(throwable)
    FapInstalledInternalLoadingState.Loading -> FapInstalledScreenState.Loading
    is FapInstalledInternalLoadingState.Loaded -> FapInstalledScreenState.Loaded(
        faps.sortedWith(
            compareBy(
                { (_, fapState) -> fapState },
                { (fapItem, _) -> fapItem.name }
            )
        ).toImmutableList()
    )

    is FapInstalledInternalLoadingState.LoadedOffline -> FapInstalledScreenState.LoadedOffline(
        faps
    )
}
