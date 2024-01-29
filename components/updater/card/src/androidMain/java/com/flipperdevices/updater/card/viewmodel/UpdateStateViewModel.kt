package com.flipperdevices.updater.card.viewmodel

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.UpdateFlipperEnd
import com.flipperdevices.metric.api.events.complex.UpdateStatus
import com.flipperdevices.updater.api.UpdateStateApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.model.FlipperUpdateState
import com.flipperdevices.updater.model.UpdatingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class UpdateStateViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider,
    private val updaterApi: UpdaterApi,
    private val metricApi: MetricApi,
    private val updateStateApi: UpdateStateApi
) : DecomposeViewModel(), FlipperBleServiceConsumer {
    private val flipperStateFlow = MutableStateFlow<FlipperUpdateState>(
        FlipperUpdateState.NotConnected
    )

    init {
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)

        updaterApi.getState().onEach {
            val updateRequest = it.request
            val endStatus = when (it.state) {
                UpdatingState.Complete -> UpdateStatus.COMPLETED
                UpdatingState.Failed -> UpdateStatus.FAILED
                else -> null
            }
            if (endStatus != null && updateRequest != null) {
                metricApi.reportComplexEvent(
                    UpdateFlipperEnd(
                        updateFrom = updateRequest.updateFrom.version,
                        updateTo = updateRequest.updateTo.version,
                        updateId = updateRequest.requestId,
                        updateStatus = endStatus
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    fun getUpdateState(): StateFlow<FlipperUpdateState> = flipperStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        updateStateApi.getFlipperUpdateState(serviceApi, viewModelScope).onEach {
            flipperStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun onDismissUpdateDialog() {
        updaterApi.resetState()
    }
}
