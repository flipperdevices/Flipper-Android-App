package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.keyedit.api.NotSavedFlipperFile
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.mapping.GridComponentStateMapper
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.viewmodel.RemoteGridViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, RemoteGridComponent.Factory::class)
class RemoteGridComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: ServerRemoteControlParam,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onSaveKey: (NotSavedFlipperKey) -> Unit,
    createRemoteGridViewModel: RemoteGridViewModel.Factory,
    createSaveTempSignalApi: Provider<SaveTempSignalApi>,
    createDispatchSignalApi: Provider<DispatchSignalApi>,
) : RemoteGridComponent, ComponentContext by componentContext {
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
    private val remoteGridViewModel = instanceKeeper.getOrCreate(
        key = "GridComponent_gridFeature_${param.key}",
        factory = {
            createRemoteGridViewModel.invoke(
                param = param,
                onCallback = { callback ->
                    when (callback) {
                        is RemoteGridViewModel.Callback.ContentLoaded -> {
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
        remoteGridViewModel.state,
        dispatchSignalApi.state,
        transform = { saveState, gridState, dispatchState ->
            GridComponentStateMapper.map(
                saveState = saveState,
                gridState = gridState,
                dispatchState = dispatchState,
            )
        }
    ).stateIn(coroutineScope, SharingStarted.Eagerly, RemoteGridComponent.Model.Loading())

    override fun dismissBusyDialog() {
        dispatchSignalApi.dismissBusyDialog()
    }

    override fun save() {
        val rawRemotes = remoteGridViewModel.getRawRemotesContent() ?: return
        val rawUi = remoteGridViewModel.getRawPagesContent() ?: return

        val notSavedFlipperFile = NotSavedFlipperKey(
            mainFile = NotSavedFlipperFile(
                FlipperFilePath(
                    folder = param.extTempFolderPath,
                    nameWithExtension = param.nameWithExtension
                ),
                content = FlipperKeyContent.RawData(rawRemotes.toByteArray())
            ),
            additionalFiles = listOf(
                NotSavedFlipperFile(
                    path = FlipperFilePath(
                        folder = param.extTempFolderPath,
                        nameWithExtension = param.uiFileNameWithExtension
                    ),
                    content = FlipperKeyContent.RawData(rawUi.toByteArray())
                )
            ),
            notes = null
        )
        onSaveKey.invoke(notSavedFlipperFile)
    }

    override fun onButtonClick(identifier: IfrKeyIdentifier) {
        val gridLoadedState =
            (remoteGridViewModel.state.value as? RemoteGridViewModel.State.Loaded) ?: return
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

    override fun tryLoad() = remoteGridViewModel.tryLoad()
    override fun pop() = onBack.invoke()
}
