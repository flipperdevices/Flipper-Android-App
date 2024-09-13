package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponse
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.buttondata.SingleKeyButtonData
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi.Companion.toDialogType
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : SetupComponent, ComponentContext by componentContext, LogTagProvider {
    override val TAG: String = "SetupComponent"
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
    private val _lastEmulatedSignal = MutableStateFlow<SignalResponse?>(null)
    override val lastEmulatedSignal: StateFlow<SignalResponse?> = _lastEmulatedSignal.asStateFlow()

    private val modelFlow = combine(
        createCurrentSignalViewModel.state,
        saveSignalApi.state,
        dispatchSignalApi.state,
        connectionViewModel.state,
        transform = { signalState, saveState, dispatchState, connectionState ->
            val emulatingState = (dispatchState as? DispatchSignalApi.State.Emulating)
            when (signalState) {
                is CurrentSignalViewModel.State.Error -> SetupComponent.Model.Error(signalState.throwable)
                is CurrentSignalViewModel.State.Loaded -> {
                    when (saveState) {
                        is SaveTempSignalApi.State.Uploading,
                        SaveTempSignalApi.State.Uploaded,
                        SaveTempSignalApi.State.Pending -> SetupComponent.Model.Loaded(
                            response = signalState.response,
                            flipperDialog = dispatchState.toDialogType(),
                            emulatedKeyIdentifier = emulatingState?.ifrKeyIdentifier,
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

    override fun dismissDialog() {
        dispatchSignalApi.dismissBusyDialog()
    }

    override fun tryLoad() {
        if (dispatchSignalApi.state.value is DispatchSignalApi.State.Emulating) return
        dispatchSignalApi.reset()
        val historyData = historyViewModel.data
        createCurrentSignalViewModel.load(
            successResults = historyData.successfulSignals,
            failedResults = historyData.failedSignals,
            skippedResults = historyData.skippedSignals
        )
        _lastEmulatedSignal.value = null
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

    override fun onSkipClicked() {
        val state = createCurrentSignalViewModel.state.value as? CurrentSignalViewModel.State.Loaded
            ?: return
        val signalModel = state.response.signalResponse?.signalModel ?: return
        historyViewModel.rememberSkipped(signalModel)
        tryLoad()
    }

    override fun dispatchSignal() {
        val state = createCurrentSignalViewModel.state.value
        val loadedState = state as? CurrentSignalViewModel.State.Loaded ?: run {
            error { "#dispatchSignal dispatch called from unloaded state" }
            return
        }
        val signalModel = loadedState.response.signalResponse?.signalModel ?: run {
            error { "#dispatchSignal signalModel is null" }
            return
        }
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
        dispatchSignalApi.dispatch(
            config = config,
            identifier = keyIdentifier,
            onDispatched = { _lastEmulatedSignal.value = loadedState.response.signalResponse }
        )
    }

    override fun forgetLastEmulatedSignal() {
        _lastEmulatedSignal.value = null
    }

    private val backCallback = BackCallback(true) {
        if (historyViewModel.isEmpty) {
            onBackClick.invoke()
        } else {
            historyViewModel.forgetLast()
        }
        tryLoad()
    }

    override fun onBackClick() = backCallback.onBack()

    init {
        backHandler.register(backCallback)
    }
}

private val ABSOLUTE_TEMP_FOLDER_PATH = "/${FlipperKeyType.INFRARED.flipperDir}/temp"
private const val TEMP_FILE_NAME = "temp.ir"
