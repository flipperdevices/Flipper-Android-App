package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.buttondata.SingleKeyButtonData
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal.mapping.toFFFormat
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.ConnectionViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.CurrentSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.HistoryViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, SetupComponent.Factory::class)
class SetupComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted override val param: SetupScreenDecomposeComponent.Param,
    @Assisted private val onBackClick: DecomposeOnBackParameter,
    @Assisted private val onIfrFileFound: (id: Long) -> Unit,
    currentSignalViewModelFactory: CurrentSignalViewModel.Factory,
    createHistoryViewModel: Provider<HistoryViewModel>,
    createSaveTempSignalApi: Provider<SaveTempSignalApi>,
    createDispatchSignalApi: Provider<DispatchSignalApi>,
    createConnectionViewModel: Provider<ConnectionViewModel>
) : SetupComponent, ComponentContext by componentContext {
    private val saveSignalApi = instanceKeeper.getOrCreate(
        key = "SetupComponent_saveSignalApi_${param.brandId}_${param.categoryId}",
        factory = {
            createSaveTempSignalApi.get()
        }
    )
    private val connectionViewModel = instanceKeeper.getOrCreate(
        key = "SetupComponent_connectionViewModel_${param.brandId}_${param.categoryId}",
        factory = {
            createConnectionViewModel.get()
        }
    )
    private val historyViewModel = instanceKeeper.getOrCreate(
        key = "SetupComponent_historyViewModel_${param.brandId}_${param.categoryId}",
        factory = {
            createHistoryViewModel.get()
        }
    )
    private val dispatchSignalApi = instanceKeeper.getOrCreate(
        key = "SetupComponent_dispatchSignalApi_${param.brandId}_${param.categoryId}",
        factory = {
            createDispatchSignalApi.get()
        }
    )
    private val createCurrentSignalViewModel = instanceKeeper.getOrCreate(
        key = "SetupComponent_createCurrentSignalViewModel_${param.brandId}_${param.categoryId}",
        factory = {
            currentSignalViewModelFactory.invoke(param) { responseModel ->
                val signalModel = responseModel.signalResponse?.signalModel ?: return@invoke
                saveSignalApi.saveFiles(
                    SaveTempSignalApi.FileDesc(
                        textContent = signalModel.toFFFormat().openStream().reader().readText(),
                        nameWithExtension = TEMP_FILE_NAME,
                        extFolderPath = ABSOLUTE_TEMP_FOLDER_PATH
                    )
                )
            }
        }
    )
    private val modelFlow = combine(
        createCurrentSignalViewModel.state,
        saveSignalApi.state,
        dispatchSignalApi.state,
        dispatchSignalApi.isEmulated,
        connectionViewModel.state,
        transform = { signalState, saveState, dispatchState, isEmulated, connectionState ->
            val emulatingState = (dispatchState as? DispatchSignalApi.State.Emulating)
            when (signalState) {
                CurrentSignalViewModel.State.Error -> SetupComponent.Model.Error
                is CurrentSignalViewModel.State.Loaded -> {
                    when (saveState) {
                        SaveTempSignalApi.State.Error -> SetupComponent.Model.Error
                        is SaveTempSignalApi.State.Uploading,
                        SaveTempSignalApi.State.Uploaded,
                        SaveTempSignalApi.State.Pending -> SetupComponent.Model.Loaded(
                            response = signalState.response,
                            isFlipperBusy = dispatchState is DispatchSignalApi.State.FlipperIsBusy,
                            emulatedKeyIdentifier = emulatingState?.ifrKeyIdentifier,
                            isEmulated = isEmulated,
                            connectionState = connectionState
                        )
                    }
                }

                CurrentSignalViewModel.State.Loading -> SetupComponent.Model.Loading(0f)
            }
        }
    ).flowOn(FlipperDispatchers.workStealingDispatcher)

    override fun model(coroutineScope: CoroutineScope) = modelFlow
        .stateIn(coroutineScope, SharingStarted.Eagerly, SetupComponent.Model.Loading(0f))

    override val remoteFoundFlow: Flow<IfrFileModel> = modelFlow
        .filterIsInstance<SetupComponent.Model.Loaded>()
        .mapNotNull { it.response.ifrFileModel }

    override fun dismissBusyDialog() {
        dispatchSignalApi.dismissBusyDialog()
    }

    override fun tryLoad() {
        if (dispatchSignalApi.state.value is DispatchSignalApi.State.Emulating) return
        dispatchSignalApi.reset()
        createCurrentSignalViewModel.load(
            successResults = historyViewModel.state.value.successfulSignals,
            failedResults = historyViewModel.state.value.failedSignals
        )
    }

    override fun onFileFound(ifrFileModel: IfrFileModel) {
        onIfrFileFound.invoke(ifrFileModel.id)
    }

    override fun onSuccessClick() {
        val state = createCurrentSignalViewModel.state.value as? CurrentSignalViewModel.State.Loaded
            ?: return
        val signalModel = state.response.signalResponse?.signalModel ?: return
        historyViewModel.rememberSuccessful(signalModel)
        tryLoad()
    }

    override fun onFailedClick() {
        val state = createCurrentSignalViewModel.state.value as? CurrentSignalViewModel.State.Loaded
            ?: return
        val signalModel = state.response.signalResponse?.signalModel ?: return
        historyViewModel.rememberFailed(signalModel)
        tryLoad()
    }

    override fun dispatchSignal() {
        val state = createCurrentSignalViewModel.state.value
        val loadedState = state as? CurrentSignalViewModel.State.Loaded ?: return
        val signalModel = loadedState.response.signalResponse?.signalModel ?: return
        val config = EmulateConfig(
            keyPath = FlipperFilePath(
                ABSOLUTE_TEMP_FOLDER_PATH,
                TEMP_FILE_NAME
            ),
            keyType = FlipperKeyType.INFRARED,
            args = signalModel.remote.name,
            index = 0
        )
        val keyIdentifier = (loadedState.response.signalResponse?.data as? SingleKeyButtonData)
            ?.keyIdentifier
            ?: IfrKeyIdentifier.Unknown
        dispatchSignalApi.dispatch(config, keyIdentifier)
    }

    override fun onBackClick() = onBackClick.invoke()
}

private val ABSOLUTE_TEMP_FOLDER_PATH = "/${FlipperKeyType.INFRARED.flipperDir}/temp"
private const val TEMP_FILE_NAME = "temp.ir"
