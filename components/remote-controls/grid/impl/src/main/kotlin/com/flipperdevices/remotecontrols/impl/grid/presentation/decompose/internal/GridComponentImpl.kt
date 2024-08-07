package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.mapping.GridComponentStateMapper
import com.flipperdevices.remotecontrols.impl.grid.presentation.util.GridParamExt.extFolderPath
import com.flipperdevices.remotecontrols.impl.grid.presentation.util.GridParamExt.extTempFolderPath
import com.flipperdevices.remotecontrols.impl.grid.presentation.util.GridParamExt.irFileIdOrNull
import com.flipperdevices.remotecontrols.impl.grid.presentation.util.GridParamExt.nameWithExtension
import com.flipperdevices.remotecontrols.impl.grid.presentation.util.GridParamExt.uiFileNameWithExtension
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.SaveDeviceViewModel
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
    @Assisted private val onPopClick: () -> Unit,
    createGridViewModel: GridViewModel.Factory,
    createSaveTempSignalApi: Provider<SaveTempSignalApi>,
    createDispatchSignalApi: Provider<DispatchSignalApi>,
    createSaveDeviceViewModel: Provider<SaveDeviceViewModel>
) : GridComponent, ComponentContext by componentContext {
    private val saveDeviceViewModel = instanceKeeper.getOrCreate(
        key = "GridComponent_saveDeviceViewModel_${param.key}",
        factory = {
            createSaveDeviceViewModel.get()
        }
    )
    private val saveTempSignalApi = instanceKeeper.getOrCreate(
        key = "GridComponent_saveSignalViewModel_${param.key}",
        factory = {
            createSaveTempSignalApi.get()
        }
    )
    private val dispatchSignalApi = instanceKeeper.getOrCreate(
        key = "GridComponent_dispatchSignalViewModel_${param.key}",
        factory = {
            createDispatchSignalApi.get()
        }
    )
    private val gridViewModel = instanceKeeper.getOrCreate(
        key = "GridComponent_gridFeature_${param.key}",
        factory = {
            createGridViewModel.invoke(
                param = param,
                onCallback = { callback ->
                    when (callback) {
                        is GridViewModel.Callback.ContentLoaded -> {
                            param.irFileIdOrNull ?: return@invoke
                            saveTempSignalApi.saveFiles(
                                SaveTempSignalApi.FileDesc(
                                    textContent = callback.infraredContent,
                                    nameWithExtension = param.nameWithExtension,
                                    extFolderPath = param.extTempFolderPath
                                ),
                                SaveTempSignalApi.FileDesc(
                                    textContent = callback.uiContent,
                                    nameWithExtension = param.uiFileNameWithExtension,
                                    extFolderPath = param.extTempFolderPath
                                )
                            )
                        }
                    }
                }
            )
        }
    )

    override fun model(coroutineScope: CoroutineScope) = combine(
        saveTempSignalApi.state,
        gridViewModel.state,
        dispatchSignalApi.state,
        saveDeviceViewModel.synchronizationState,
        transform = { saveState, gridState, dispatchState, synchronizationState ->
            GridComponentStateMapper.map(
                saveState = saveState,
                gridState = gridState,
                dispatchState = dispatchState,
                synchronizationState = synchronizationState
            )
        }
    ).stateIn(coroutineScope, SharingStarted.Eagerly, GridComponent.Model.Loading())

    override fun dismissBusyDialog() {
        dispatchSignalApi.dismissBusyDialog()
    }

    override fun save() {
        val rawRemotes = gridViewModel.getRawRemotesContent() ?: return
        val rawUi = gridViewModel.getRawPagesContent() ?: return
        saveTempSignalApi.saveFiles(
            SaveTempSignalApi.FileDesc(
                textContent = rawRemotes,
                nameWithExtension = param.nameWithExtension,
                extFolderPath = param.extFolderPath
            ),
            SaveTempSignalApi.FileDesc(
                textContent = rawUi,
                nameWithExtension = param.uiFileNameWithExtension,
                extFolderPath = param.extFolderPath
            ),
            onFinished = {
                saveDeviceViewModel.saveToDatabase(
                    remotesPath = FlipperFilePath(
                        folder = param.extFolderPath,
                        nameWithExtension = param.nameWithExtension
                    ),
                    uiPath = FlipperFilePath(
                        folder = param.extFolderPath,
                        nameWithExtension = param.uiFileNameWithExtension
                    ),
                    rawRemotes = rawRemotes,
                    rawUi = rawUi
                )
            }
        )
    }

    override fun onButtonClick(identifier: IfrKeyIdentifier) {
        val gridLoadedState = (gridViewModel.state.value as? GridViewModel.State.Loaded) ?: return
        val remotes = gridLoadedState.remotes
        dispatchSignalApi.dispatch(
            identifier = identifier,
            remotes = remotes,
            ffPath = FlipperFilePath(
                folder = param.extTempFolderPath,
                nameWithExtension = param.nameWithExtension
            )
        )
    }

    override fun tryLoad() = gridViewModel.tryLoad()
    override fun pop() = onPopClick.invoke()
}
