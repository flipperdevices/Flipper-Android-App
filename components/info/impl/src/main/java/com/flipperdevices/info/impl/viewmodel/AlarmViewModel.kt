package com.flipperdevices.info.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.playAudiovisualAlertRequest
import javax.inject.Inject
import kotlinx.coroutines.launch

class AlarmViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    fun alarmOnFlipper() {
        serviceProvider.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch {
                alarmOnFlipper(serviceApi)
            }
        }
    }

    private suspend fun alarmOnFlipper(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.requestWithoutAnswer(
            main {
                systemPlayAudiovisualAlertRequest = playAudiovisualAlertRequest { }
            }.wrapToRequest()
        )
    }
}
