package com.flipperdevices.faphub.installedtab.impl.model

import com.flipperdevices.faphub.installedtab.impl.viewmodel.FapInstalledInternalLoadingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

sealed class FapInstalledScreenState {
    data class Loaded(
        val faps: ImmutableList<Pair<InstalledFapApp, FapInstalledInternalState>>,
        val inProgress: Boolean,
        val networkError: InstalledNetworkErrorEnum? = null
    ) : FapInstalledScreenState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledScreenState()
}

fun FapInstalledInternalLoadingState.toScreenState() = when (this) {
    is FapInstalledInternalLoadingState.Error -> FapInstalledScreenState.Error(throwable)
    is FapInstalledInternalLoadingState.Loaded -> FapInstalledScreenState.Loaded(
        faps = faps
            .distinctBy { it.first.applicationUid }
            .sortedWith(
                compareBy(
                    { (_, fapState) -> fapState },
                    { (fapItem, _) -> fapItem.name }
                )
            ).toImmutableList(),
        inProgress = inProgress,
        networkError = networkError
    )
}
