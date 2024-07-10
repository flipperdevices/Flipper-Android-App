package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, GridComponent.Factory::class)
class GridComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: GridScreenDecomposeComponent.Param,
    @Assisted private val onPopClicked: () -> Unit,
    createGridViewModel: GridViewModel.Factory,
    createSaveSignalViewModel: Provider<SaveTempSignalApi>,
    createDispatchSignalViewModel: Provider<DispatchSignalApi>
) : GridComponent, ComponentContext by componentContext {
    private val saveSignalViewModel = instanceKeeper.getOrCreate(
        key = "GridComponent_saveSignalViewModel_${param.ifrFileId}_${param.uiFileId}",
        factory = {
            createSaveSignalViewModel.get()
        }
    )
    private val dispatchSignalViewModel = instanceKeeper.getOrCreate(
        key = "GridComponent_dispatchSignalViewModel_${param.ifrFileId}_${param.uiFileId}",
        factory = {
            createDispatchSignalViewModel.get()
        }
    )
    private val gridFeature = instanceKeeper.getOrCreate(
        key = "GridComponent_gridFeature_${param.ifrFileId}_${param.uiFileId}",
        factory = {
            createGridViewModel.invoke(
                param = param,
                onIrFileLoaded = { content ->
                    val fff = FlipperFileFormat.fromFileContent(content)
                    saveSignalViewModel.saveTempFile(
                        fff = fff,
                        nameWithExtension = "${param.ifrFileId}.ir"
                    )
                }
            )
        }
    )

    override fun model(coroutineScope: CoroutineScope) = combine(
        saveSignalViewModel.state,
        gridFeature.state,
        transform = { saveState, gridState ->
            when (gridState) {
                GridViewModel.State.Error -> GridComponent.Model.Error
                is GridViewModel.State.Loaded -> {
                    when (saveState) {
                        SaveTempSignalApi.State.Error -> GridComponent.Model.Error
                        SaveTempSignalApi.State.Uploaded, SaveTempSignalApi.State.Pending -> {
                            GridComponent.Model.Loaded(
                                pagesLayout = gridState.pagesLayout,
                                remotes = gridState.remotes
                            )
                        }

                        is SaveTempSignalApi.State.Uploading -> GridComponent.Model.Loading(
                            saveState.progress
                        )
                    }
                }

                GridViewModel.State.Loading -> GridComponent.Model.Error
            }
        }
    ).stateIn(coroutineScope, SharingStarted.Eagerly, GridComponent.Model.Loading(0f))

    override fun flipperState(coroutineScope: CoroutineScope) = combine(
        dispatchSignalViewModel.state,
        transform = { (dispatchState) ->
            GridComponent.FlipperState(
                isFlipperBusy = dispatchState is DispatchSignalApi.State.FlipperIsBusy,
                isEmulating = dispatchState is DispatchSignalApi.State.Emulating
            )
        }
    ).stateIn(coroutineScope, SharingStarted.Eagerly, GridComponent.FlipperState())

    override fun dismissBusyDialog() {
        dispatchSignalViewModel.dismissBusyDialog()
    }

    override fun onButtonClicked(identifier: IfrKeyIdentifier) {
        val gridLoadedState = (gridFeature.state.value as? GridViewModel.State.Loaded) ?: return
        val remotes = gridLoadedState.remotes
        dispatchSignalViewModel.dispatch(
            identifier = identifier,
            remotes = remotes,
            ffPath = FlipperFilePath(
                folder = FLIPPER_TEMP_FOLDER,
                nameWithExtension = "${param.ifrFileId}.ir"
            )
        )
    }

    override fun tryLoad() = gridFeature.tryLoad()
    override fun pop() = onPopClicked.invoke()
}

private val FLIPPER_TEMP_FOLDER = FlipperKeyType.INFRARED.flipperDir + "/temp"
