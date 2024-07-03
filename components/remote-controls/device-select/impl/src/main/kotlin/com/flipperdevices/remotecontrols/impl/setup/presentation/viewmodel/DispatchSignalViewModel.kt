package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.backend.model.SignalModel
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel.SaveSignalViewModel.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DispatchSignalViewModel(
    private val emulateHelper: EmulateHelper,
    private val serviceProvider: FlipperServiceProvider
) : DecomposeViewModel(), FlipperBleServiceConsumer, LogTagProvider {
    override val TAG: String = "DispatchSignalViewModel"

    val state = MutableStateFlow<State>(State.Pending)

    fun reset() {
        state.value = State.Pending
    }

    fun dispatch(signalModel: SignalModel) {
        val ffPath = FlipperFilePath(
            FlipperKeyType.INFRARED.flipperDir,
            "ifr_temp.ir"
        )
        val config = EmulateConfig(
            keyPath = ffPath,
            keyType = FlipperKeyType.INFRARED,
            args = signalModel.name,
            index = 0
        )
        serviceProvider.provideServiceApi(
            lifecycleOwner = this,
            onError = { state.value = State.Error },
            onBleManager = { serviceApi ->
                viewModelScope.launch {
                    state.value = State.Emulating
                    kotlin.runCatching {
                        emulateHelper.startEmulate(
                            scope = this,
                            serviceApi = serviceApi,
                            config = config
                        )
                        delay(500L)
                        emulateHelper.stopEmulate(this, serviceApi.requestApi)
                    }.onFailure(Throwable::printStackTrace) // todo
                    reset()
                }
            }
        )
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit

    sealed interface State {
        data object Pending : State
        data object Emulating : State
        data object Error : State
    }
}
