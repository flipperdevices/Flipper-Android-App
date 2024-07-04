package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.CurrentSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.DispatchSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.HistoryViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.SaveSignalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

internal class SetupComponentImpl(
    componentContext: ComponentContext,
    override val param: SetupScreenDecomposeComponent.Param,
    private val onBackClicked: () -> Unit,
    private val onIfrFileFound: (ifrFileId: Long) -> Unit,
    createCurrentSignalViewModel: (onLoaded: (SignalResponseModel) -> Unit) -> CurrentSignalViewModel,
    createHistoryViewModel: () -> HistoryViewModel,
    createSaveSignalViewModel: () -> SaveSignalViewModel,
    createDispatchSignalViewModel: () -> DispatchSignalViewModel
) : SetupComponent, ComponentContext by componentContext {
    private val saveFileViewModel = instanceKeeper.getOrCreate {
        createSaveSignalViewModel.invoke()
    }
    private val signalFeature = instanceKeeper.getOrCreate {
        createCurrentSignalViewModel.invoke {
            it.signalResponse?.signalModel?.let { signalModel ->
                saveFileViewModel.save(signalModel, "ir_temp.ir")
            }
        }
    }
    private val historyFeature = instanceKeeper.getOrCreate {
        createHistoryViewModel.invoke()
    }
    private val dispatchSignalViewModel = instanceKeeper.getOrCreate {
        createDispatchSignalViewModel.invoke()
    }
    private val modelFlow = combine(
        signalFeature.state,
        saveFileViewModel.state,
        transform = { signalState, saveState ->
            when (signalState) {
                CurrentSignalViewModel.State.Error -> SetupComponent.Model.Error
                is CurrentSignalViewModel.State.Loaded -> {
                    when (saveState) {
                        SaveSignalViewModel.State.Error -> SetupComponent.Model.Error
                        SaveSignalViewModel.State.Pending -> SetupComponent.Model.Loaded(
                            response = signalState.response
                        )

                        SaveSignalViewModel.State.Uploaded -> SetupComponent.Model.Loaded(
                            response = signalState.response
                        )

                        is SaveSignalViewModel.State.Uploading -> SetupComponent.Model.Loading(
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
        saveFileViewModel.reset()
        signalFeature.load(
            successResults = historyFeature.state.value.successfulSignals,
            failedResults = historyFeature.state.value.failedSignals
        )
    }

    override fun onFileFound(ifrFileModel: IfrFileModel) {
        onIfrFileFound.invoke(ifrFileModel.id)
    }

    override fun onSuccessClicked() {
        val state = signalFeature.state.value as CurrentSignalViewModel.State.Loaded ?: return
        val signalModel = state.response.signalResponse?.signalModel ?: return
        historyFeature.rememberSuccessful(signalModel)
        tryLoad()
    }

    override fun onFailedClicked() {
        val state = signalFeature.state.value as CurrentSignalViewModel.State.Loaded ?: return
        val signalModel = state.response.signalResponse?.signalModel ?: return
        historyFeature.rememberFailed(signalModel)
        tryLoad()
    }

    override fun dispatchSignal() {
        val state = signalFeature.state.value
        val loadedState = state as? CurrentSignalViewModel.State.Loaded ?: return
        val signalModel = loadedState.response.signalResponse?.signalModel ?: return
        dispatchSignalViewModel.dispatch(signalModel)
    }

    override fun onBackClicked() = onBackClicked.invoke()
}
