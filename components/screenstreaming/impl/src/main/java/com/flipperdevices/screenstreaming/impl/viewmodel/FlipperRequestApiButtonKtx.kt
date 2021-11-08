package com.flipperdevices.screenstreaming.impl.viewmodel

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.sendInputEventRequest
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun FlipperRequestApi.pressOnButton(
    viewModelScope: CoroutineScope,
    buttonEnum: ButtonEnum,
    type: Gui.InputType
) = viewModelScope.launch {
    requestWithoutAnswer(
        getRequestFor(buttonEnum, Gui.InputType.PRESS),
        getRequestFor(buttonEnum, type),
        getRequestFor(buttonEnum, Gui.InputType.RELEASE)
    )
}

private fun getRequestFor(
    buttonEnum: ButtonEnum,
    buttonType: Gui.InputType
): Flipper.Main {
    return main {
        guiSendInputEventRequest = sendInputEventRequest {
            key = buttonEnum.key
            type = buttonType
        }
    }
}
