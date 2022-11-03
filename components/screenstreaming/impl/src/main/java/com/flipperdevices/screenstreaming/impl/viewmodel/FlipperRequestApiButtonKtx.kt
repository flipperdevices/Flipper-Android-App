package com.flipperdevices.screenstreaming.impl.viewmodel

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.sendInputEventRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Suppress("SpreadOperator")
internal fun FlipperRequestApi.pressOnButton(
    viewModelScope: CoroutineScope,
    key: Gui.InputKey,
    type: Gui.InputType,
    times: Int = 1
) = viewModelScope.launch {
    val requests = mutableListOf<FlipperRequest>()
    repeat(times) {
        requests.add(getRequestFor(key, Gui.InputType.PRESS))
        requests.add(getRequestFor(key, type))
        requests.add(getRequestFor(key, Gui.InputType.RELEASE))
    }
    requestWithoutAnswer(*requests.toTypedArray())
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
