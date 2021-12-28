package com.flipperdevices.debug.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.debug.impl.di.DebugComponent
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.playAudiovisualAlertRequest
import javax.inject.Inject
import kotlinx.coroutines.launch

class DebugViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<DebugComponent>().inject(this)
    }

    fun sendAlarmToFlipper() {
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch {
                it.requestApi.requestWithoutAnswer(
                    main {
                        systemPlayAudiovisualAlertRequest = playAudiovisualAlertRequest { }
                    }.wrapToRequest()
                )
            }
        }
    }
}
