package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi.Companion.saveFile
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.mapping.GridComponentStateMapper
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, GridComponent.Factory::class)
class GridComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: GridScreenDecomposeComponent.Param,
    @Assisted private val onPopClick: () -> Unit,
    createGridViewModel: GridViewModel.Factory,
    createSaveSignalViewModel: Provider<SaveTempSignalApi>,
    createDispatchSignalViewModel: Provider<DispatchSignalApi>
) : GridComponent, ComponentContext by componentContext {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }
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
                onUiLoaded = { pagesLayout ->
                    saveSignalViewModel.saveFile(
                        deeplinkContent = DeeplinkContent.Raw(
                            filename = "template.ui.json",
                            content = json.encodeToString(pagesLayout)
                        ),
                        nameWithExtension = "template.ui.json",
                        folderName = "/temp/${param.ifrFileId}"
                    )
                },
                onIrFileLoaded = { content ->
                    val fff = FlipperFileFormat.fromFileContent(content)
                    saveSignalViewModel.saveFile(
                        fff = fff,
                        nameWithExtension = "${param.ifrFileId}.ir",
                        folderName = "/temp/${param.ifrFileId}"
                    )
                }
            )
        }
    )

    override fun model(coroutineScope: CoroutineScope) = combine(
        saveSignalViewModel.state,
        gridFeature.state,
        dispatchSignalViewModel.state,
        transform = { saveState, gridState, dispatchState ->
            GridComponentStateMapper.map(
                saveState = saveState,
                gridState = gridState,
                dispatchState = dispatchState
            )
        }
    ).stateIn(coroutineScope, SharingStarted.Eagerly, GridComponent.Model.Loading())

    override fun dismissBusyDialog() {
        dispatchSignalViewModel.dismissBusyDialog()
    }

    override fun onButtonClick(identifier: IfrKeyIdentifier) {
        val gridLoadedState = (gridFeature.state.value as? GridViewModel.State.Loaded) ?: return
        val remotes = gridLoadedState.remotes
        dispatchSignalViewModel.dispatch(
            identifier = identifier,
            remotes = remotes,
            ffPath = FlipperFilePath(
                folder = "${FlipperKeyType.INFRARED.flipperDir}/temp/${param.ifrFileId}",
                nameWithExtension = "${param.ifrFileId}.ir"
            )
        )
    }

    override fun onDeleteFile() {
        gridFeature.delete()
    }

    override fun onSaveFile() {
        gridFeature.saveSignal()
    }

    override fun tryLoad() = gridFeature.tryLoad()
    override fun pop() = onPopClick.invoke()
}
