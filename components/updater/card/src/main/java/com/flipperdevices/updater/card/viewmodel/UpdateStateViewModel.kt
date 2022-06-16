package com.flipperdevices.updater.card.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.toIntSafe
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.UpdateFlipperEnd
import com.flipperdevices.metric.api.events.complex.UpdateStatus
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.card.di.CardComponent
import com.flipperdevices.updater.card.model.FlipperState
import com.flipperdevices.updater.model.UpdatingState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UpdateStateViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val flipperStateFlow = MutableStateFlow<FlipperState>(FlipperState.NOT_READY)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var updaterApi: UpdaterApi

    @Inject
    lateinit var metricApi: MetricApi

    init {
        ComponentHolder.component<CardComponent>().inject(this)
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
                        updateTo = updateRequest.updateTo.version.version,
                        updateId = updateRequest.requestId.toIntSafe(),
                        updateStatus = endStatus
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    fun getState(): StateFlow<FlipperState> = flipperStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        combine(
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            serviceApi.flipperInformationApi.getInformationFlow(),
            updaterApi.getState()
        ) { connectionState, informationState, updaterState ->
            val isReady = connectionState is ConnectionState.Ready &&
                    connectionState.isSupported
            val version = informationState.softwareVersion

            if (isReady && version != null) {
                return@combine when (updaterState.state) {
                    is UpdatingState.Rebooting -> {
                        updaterApi.onDeviceConnected(version)
                        FlipperState.READY
                    }
                    is UpdatingState.Complete -> {
                        FlipperState.COMPLETE
                    }
                    is UpdatingState.Failed -> {
                        FlipperState.FAILED
                    }
                    else -> FlipperState.READY
                }
            }
            return@combine if (updaterState.state is UpdatingState.Rebooting) {
                FlipperState.UPDATING
            } else FlipperState.NOT_READY
        }.onEach {
            flipperStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }
}
