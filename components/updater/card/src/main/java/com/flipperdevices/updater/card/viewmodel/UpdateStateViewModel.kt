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
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.card.di.CardComponent
import com.flipperdevices.updater.card.model.FlipperUpdateState
import com.flipperdevices.updater.model.UpdatingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class UpdateStateViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val flipperStateFlow = MutableStateFlow<FlipperUpdateState>(FlipperUpdateState.NotReady)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var updaterApi: UpdaterApi

    @Inject
    lateinit var metricApi: MetricApi

    @Inject
    lateinit var versionParser: FlipperVersionProviderApi

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

    fun getState(): StateFlow<FlipperUpdateState> = flipperStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        combine(
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            versionParser.getCurrentFlipperVersion(viewModelScope, serviceApi),
            updaterApi.getState()
        ) { connectionState, flipperVersion, updaterState ->
            val isReady = connectionState is ConnectionState.Ready &&
                connectionState.isSupported

            if (isReady && flipperVersion != null) {
                return@combine when (updaterState.state) {
                    is UpdatingState.Rebooting -> {
                        updaterApi.onDeviceConnected(
                            flipperVersion
                        )
                        FlipperUpdateState.Ready
                    }
                    is UpdatingState.Complete -> {
                        FlipperUpdateState.Complete(updaterState.request?.updateTo?.version)
                    }
                    is UpdatingState.Failed -> {
                        FlipperUpdateState.Failed(updaterState.request?.updateTo?.version)
                    }
                    else -> FlipperUpdateState.Ready
                }
            }
            return@combine if (updaterState.state is UpdatingState.Rebooting) {
                FlipperUpdateState.Updating
            } else FlipperUpdateState.NotReady
        }.onEach {
            flipperStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun onDismissUpdateDialog() {
        updaterApi.resetState()
    }
}
