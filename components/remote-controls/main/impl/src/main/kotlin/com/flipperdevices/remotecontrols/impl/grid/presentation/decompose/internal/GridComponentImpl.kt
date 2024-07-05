package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SaveSignalApi
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class GridComponentImpl(
    componentContext: ComponentContext,
    private val param: GridScreenDecomposeComponent.Param,
    createGridViewModel: (onIrFileLoaded: (String) -> Unit) -> GridViewModel,
    createSaveSignalViewModel: () -> SaveSignalApi,
    createDispatchSignalViewModel: () -> DispatchSignalApi,
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
            val fff = FlipperFileFormat.fromFileContent(content)
            saveSignalViewModel.save(fff, "${param.ifrFileId}.ir")
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
                        SaveSignalApi.State.Error -> GridComponent.Model.Error
                        SaveSignalApi.State.Uploaded, SaveSignalApi.State.Pending -> {
                            GridComponent.Model.Loaded(
                                pagesLayout = gridState.pagesLayout,
                                remotes = gridState.remotes
                            )
                        }

                        is SaveSignalApi.State.Uploading -> GridComponent.Model.Loading(
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
