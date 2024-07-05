package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.api.SaveSignalApi
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.CurrentSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.HistoryViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider
private const val TEMP_FILE_NAME = "temp.ir"

@ContributesAssistedFactory(AppGraph::class, SetupComponent.Factory::class)
class SetupComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted override val param: SetupScreenDecomposeComponent.Param,
    @Assisted private val onBackClicked: () -> Unit,
    @Assisted private val onIfrFileFound: (ifrFileId: Long) -> Unit,
    currentSignalViewModelFactory: CurrentSignalViewModel.Factory,
    createHistoryViewModel: Provider<HistoryViewModel>,
    createSaveSignalApi: Provider<SaveSignalApi>,
    createDispatchSignalApi: Provider<DispatchSignalApi>
) : SetupComponent, ComponentContext by componentContext {
    private val saveSignalApi = instanceKeeper.getOrCreate {
        createSaveSignalApi.get()
    }
    private val historyViewModel = instanceKeeper.getOrCreate {
        createHistoryViewModel.get()
    }
    private val dispatchSignalApi = instanceKeeper.getOrCreate {
        createDispatchSignalApi.get()
    }
    private val createCurrentSignalViewModel = instanceKeeper.getOrCreate {
        currentSignalViewModelFactory.invoke(param) {
            it.signalResponse?.signalModel?.let { signalModel ->
                val fff = FlipperFileFormat(
                    orderedDict = listOf(
                        ("Filetype" to "IR signals file"),
                        ("Version" to "1"),
                        ("name" to signalModel.name),
                        ("type" to signalModel.type),
                        ("frequency" to signalModel.frequency),
                        ("duty_cycle" to signalModel.dutyCycle),
                        ("data" to signalModel.data),
                        ("protocol" to signalModel.protocol),
                        ("address" to signalModel.address),
                        ("command" to signalModel.command),
                    ).mapNotNull { (k, v) -> if (v == null) null else k to v }
                )
                saveSignalApi.save(fff, TEMP_FILE_NAME)
            }
        }
    }
    private val modelFlow = combine(
        this.createCurrentSignalViewModel.state,
        saveSignalApi.state,
        transform = { signalState, saveState ->
            when (signalState) {
                CurrentSignalViewModel.State.Error -> SetupComponent.Model.Error
                is CurrentSignalViewModel.State.Loaded -> {
                    when (saveState) {
                        SaveSignalApi.State.Error -> SetupComponent.Model.Error
                        SaveSignalApi.State.Pending -> SetupComponent.Model.Loaded(
                            response = signalState.response
                        )

                        SaveSignalApi.State.Uploaded -> SetupComponent.Model.Loaded(
                            response = signalState.response
                        )

                        is SaveSignalApi.State.Uploading -> SetupComponent.Model.Loading(
                            saveState.progress
                        )
                    }
                }

                CurrentSignalViewModel.State.Loading -> SetupComponent.Model.Loading(0f)
            }
        }
    ).flowOn(Dispatchers.IO)

    override fun model(coroutineScope: CoroutineScope) = modelFlow
        .stateIn(coroutineScope, SharingStarted.Eagerly, SetupComponent.Model.Loading(0f))

    override val remoteFoundFlow: Flow<IfrFileModel> = modelFlow
        .filterIsInstance<SetupComponent.Model.Loaded>()
        .mapNotNull { it.response.ifrFileModel }

    override fun tryLoad() {
        createCurrentSignalViewModel.load(
            successResults = historyViewModel.state.value.successfulSignals,
            failedResults = historyViewModel.state.value.failedSignals
        )
    }

    override fun onFileFound(ifrFileModel: IfrFileModel) {
        onIfrFileFound.invoke(ifrFileModel.id)
    }

    override fun onSuccessClicked() {
        val state = createCurrentSignalViewModel.state.value as CurrentSignalViewModel.State.Loaded ?: return
        val signalModel = state.response.signalResponse?.signalModel ?: return
        historyViewModel.rememberSuccessful(signalModel)
        tryLoad()
    }

    override fun onFailedClicked() {
        val state = createCurrentSignalViewModel.state.value as CurrentSignalViewModel.State.Loaded ?: return
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
                FlipperKeyType.INFRARED.flipperDir,
                TEMP_FILE_NAME
            ),
            keyType = FlipperKeyType.INFRARED,
            args = signalModel.name,
            index = 0
        )
        dispatchSignalApi.dispatch(config)
    }

    override fun onBackClicked() = onBackClicked.invoke()
}
