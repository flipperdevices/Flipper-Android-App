package com.flipperdevices.updater.card.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.card.di.CardComponent
import com.flipperdevices.updater.card.model.BatteryState
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateRequest
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UpdateRequestViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val batteryStateFlow = MutableStateFlow<BatteryState>(BatteryState.Unknown)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var updaterUIApi: UpdaterUIApi

    init {
        ComponentHolder.component<CardComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getBatteryState(): StateFlow<BatteryState> = batteryStateFlow

    fun openUpdate(updateAvailable: UpdateCardState.UpdateAvailable) {
        updaterUIApi.openUpdateScreen(
            silent = false,
            updateRequest = UpdateRequest(
                updateFrom = updateAvailable.fromVersion,
                updateTo = updateAvailable.lastVersion
            )
        )
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperInformationApi.getInformationFlow().onEach {
            val batteryLevel = it.batteryLevel
            if (batteryLevel != null) {
                batteryStateFlow.emit(BatteryState.Ready(it.isCharging, batteryLevel))
            } else batteryStateFlow.emit(BatteryState.Unknown)
        }.launchIn(viewModelScope)
    }
}
