package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.CurrentSignalViewModel
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.HistoryViewModel
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
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
    createCurrentSignalViewModel: () -> CurrentSignalViewModel,
    createHistoryViewModel: () -> HistoryViewModel,
) : SetupComponent, ComponentContext by componentContext {
    private val signalFeature = instanceKeeper.getOrCreate {
        createCurrentSignalViewModel.invoke()
    }
    private val historyFeature = instanceKeeper.getOrCreate {
        createHistoryViewModel.invoke()
    }
    private val modelFlow = combine(
        signalFeature.state,
        transform = { (state) ->
            when (state) {
                CurrentSignalViewModel.State.Error -> SetupComponent.Model.Error
                is CurrentSignalViewModel.State.Loaded -> {
                    SetupComponent.Model.Loaded(response = state.response)
                }

                CurrentSignalViewModel.State.Loading -> SetupComponent.Model.Loading
            }
        }
    ).flowOn(Dispatchers.IO)

    override fun model(coroutineScope: CoroutineScope) = modelFlow
        .stateIn(coroutineScope, SharingStarted.Eagerly, SetupComponent.Model.Loading)


    override val remoteFoundFlow: Flow<IfrFileModel> = modelFlow
        .filterIsInstance<SetupComponent.Model.Loaded>()
        .mapNotNull { it.response.ifrFileModel }

    override fun tryLoad() {
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

    // todo
    override fun dispatchSignal() = Unit

    override fun onBackClicked() = onBackClicked.invoke()
}
