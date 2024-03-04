package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installedtab.impl.viewmodel.FapInstalledInternalLoadingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal sealed class FapInstalledScreenState {
    data object Loading : FapInstalledScreenState()

    data class Loaded(
        val faps: ImmutableList<Pair<InstalledFapApp, FapInstalledInternalState>>
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
}
