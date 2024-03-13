package com.flipperdevices.faphub.installedtab.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.faphub.installedtab.api.FapUpdatePendingCountApi
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.FapInstalledInternalLoadingState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsFromNetworkProducer
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, FapUpdatePendingCountApi::class)
class FapUpdatePendingCountApiImpl @Inject constructor(
    fapsStateProducerProvider: Provider<InstalledFapsFromNetworkProducer>
) : FapUpdatePendingCountApi {
    private val fapsStateProducer by fapsStateProducerProvider

    override fun getUpdatePendingCount(): Flow<Int> {
        fapsStateProducer.refresh(force = false)
        return fapsStateProducer.getLoadedFapsFlow().map { loadingState ->
            when (loadingState) {
                is FapInstalledInternalLoadingState.Error -> 0

                is FapInstalledInternalLoadingState.Loaded -> loadingState.faps.count { (_, state) ->
                    state is FapInstalledInternalState.ReadyToUpdate
                }
            }
        }
    }
}
