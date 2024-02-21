package com.flipperdevices.screenstreaming.impl.viewmodel.repository

import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.sendInputEventRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

class FlipperButtonRepository @Inject constructor(
    private val serviceProvider: FlipperServiceProvider
) : LogTagProvider {
    override val TAG: String = "FlipperButtonRequest"

    private val mutex = Mutex()

    fun pressOnButton(
        viewModelScope: CoroutineScope,
        key: Gui.InputKey,
        type: Gui.InputType,
        onComplete: () -> Unit = {}
    ) = launchWithLock(mutex = mutex, scope = viewModelScope) {
        val requestApi = serviceProvider.getServiceApi().requestApi

        requestApi.requestWithoutAnswer(
            getRequestFor(key, Gui.InputType.PRESS),
            getRequestFor(key, type)
        )

        requestApi.request(getRequestFor(key, Gui.InputType.RELEASE))
            .onEach { onComplete() }
            .launchIn(viewModelScope)
    }

    private fun getRequestFor(
        inputKey: Gui.InputKey,
        buttonType: Gui.InputType
    ): FlipperRequest {
        return main {
            guiSendInputEventRequest = sendInputEventRequest {
                key = inputKey
                type = buttonType
            }
        }.wrapToRequest()
    }
}
