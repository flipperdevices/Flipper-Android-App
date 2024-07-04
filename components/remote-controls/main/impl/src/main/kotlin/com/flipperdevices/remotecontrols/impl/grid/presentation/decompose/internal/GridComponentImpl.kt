package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.DispatchSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.SaveSignalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class GridComponentImpl(
    componentContext: ComponentContext,
    private val param: GridScreenDecomposeComponent.Param,
    createGridViewModel: (onIrFileLoaded: (String) -> Unit) -> GridViewModel,
    createSaveSignalViewModel: () -> SaveSignalViewModel,
    createDispatchSignalViewModel: () -> DispatchSignalViewModel,
    private val onPopClicked: () -> Unit
) : GridComponent, ComponentContext by componentContext {
    private val saveSignalViewModel = instanceKeeper.getOrCreate {
        createSaveSignalViewModel.invoke()
    }
    private val dispatchSignalViewModel = instanceKeeper.getOrCreate {
        createDispatchSignalViewModel.invoke()
    }
    private val gridFeature = instanceKeeper.getOrCreate {
        createGridViewModel.invoke { content ->
            saveSignalViewModel.save(content, "${param.ifrFileId}.ir")
        }
    }

    override fun model(coroutineScope: CoroutineScope) = combine(
        saveSignalViewModel.state,
        gridFeature.state,
        transform = { saveState, gridState ->
            when (gridState) {
                GridViewModel.State.Error -> GridComponent.Model.Error
                is GridViewModel.State.Loaded -> {
                    when (saveState) {
                        SaveSignalViewModel.State.Error -> GridComponent.Model.Error
                        SaveSignalViewModel.State.Uploaded, SaveSignalViewModel.State.Pending -> {
                            GridComponent.Model.Loaded(
                                pagesLayout = gridState.pagesLayout,
                                remotes = gridState.remotes
                            )
                        }

                        is SaveSignalViewModel.State.Uploading -> GridComponent.Model.Loading(
                            saveState.progress
                        )
                    }
                }

                GridViewModel.State.Loading -> GridComponent.Model.Error
            }
        }
    ).stateIn(coroutineScope, SharingStarted.Eagerly, GridComponent.Model.Loading(0f))

    override fun onButtonClicked(identifier: IfrKeyIdentifier) {
        val gridLoadedState = (gridFeature.state.value as? GridViewModel.State.Loaded) ?: return
        val remotes = gridLoadedState.remotes
        dispatchSignalViewModel.dispatch(identifier, remotes, "${param.ifrFileId}.ir")
    }

    override fun tryLoad() = gridFeature.tryLoad()
    override fun pop() = onPopClicked.invoke()
}
