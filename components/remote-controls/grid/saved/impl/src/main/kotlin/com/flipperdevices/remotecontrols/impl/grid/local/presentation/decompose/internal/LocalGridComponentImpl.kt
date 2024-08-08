package com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.viewmodel.LocalGridViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, LocalGridComponent.Factory::class)
class LocalGridComponentImpl @AssistedInject constructor(
    @Assisted private val componentContext: ComponentContext,
    @Assisted private val param: GridControlParam.Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    createLocalGridViewModel: LocalGridViewModel.Factory,
    createDispatchSignalApi: Provider<DispatchSignalApi>,
    private val synchronizationApi: SynchronizationApi
) : LocalGridComponent, ComponentContext by componentContext {
    private val localGridViewModel = instanceKeeper.getOrCreate(
        key = "LocalGridComponent_localGridViewModel_${param.key}",
        factory = { createLocalGridViewModel.invoke(param) }
    )
    private val dispatchSignalApi = instanceKeeper.getOrCreate(
        key = "LocalGridComponent_dispatchSignalApi_${param.key}",
        factory = { createDispatchSignalApi.get() }
    )

    override fun model(coroutineScope: CoroutineScope) = combine(
        flow = localGridViewModel.state,
        flow2 = dispatchSignalApi.state,
        flow3 = synchronizationApi.getSynchronizationState(),
        transform = { gridState, dispatchState, syncState ->
            when (gridState) {
                LocalGridViewModel.State.Error -> LocalGridComponent.Model.Error
                is LocalGridViewModel.State.Loaded -> LocalGridComponent.Model.Loaded(
                    pagesLayout = gridState.pagesLayout,
                    remotes = gridState.remotes,
                    isFlipperBusy = dispatchState is DispatchSignalApi.State.FlipperIsBusy,
                    emulatedKey = (dispatchState as? DispatchSignalApi.State.Emulating)?.ifrKeyIdentifier,
                    synchronizationState = syncState
                )

                LocalGridViewModel.State.Loading -> LocalGridComponent.Model.Loading
            }
        }
    ).stateIn(coroutineScope, SharingStarted.Eagerly, LocalGridComponent.Model.Loading)

    override fun onButtonClick(identifier: IfrKeyIdentifier) {
        val gridLoadedState =
            (localGridViewModel.state.value as? LocalGridViewModel.State.Loaded) ?: return
        val remotes = gridLoadedState.remotes
        dispatchSignalApi.dispatch(
            identifier = identifier,
            remotes = remotes,
            ffPath = FlipperFilePath(
                folder = param.extFolderPath,
                nameWithExtension = param.nameWithExtension
            )
        )
    }

    override fun pop() = onBack.invoke()

    override fun dismissBusyDialog() {
        dispatchSignalApi.dismissBusyDialog()
    }
}