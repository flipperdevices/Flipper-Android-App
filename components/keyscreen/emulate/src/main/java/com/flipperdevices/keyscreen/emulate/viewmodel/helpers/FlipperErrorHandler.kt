package com.flipperdevices.keyscreen.emulate.viewmodel.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.getErrorRequest
import com.flipperdevices.protobuf.main
import kotlinx.coroutines.flow.flowOf

class FlipperErrorHandler {
    suspend fun requestError(

        priority: FlipperRequestPriority,
        requestApi: FlipperRequestApi
    ): FlipperError {
        val errorResponse = requestApi.request(
            flowOf(
                main {
                    appGetErrorRequest = getErrorRequest {}
                }.wrapToRequest(priority)
            )
        )
        return when (errorResponse.commandStatus) {
            Flipper.CommandStatus.OK -> {
                val errorCode = errorResponse.appGetErrorResponse.code
                val errorText = errorResponse.appGetErrorResponse.text
                processFlipperErrorData(errorCode, errorText)
            }

            else -> FlipperError.BadResponse
        }
    }

    private fun processFlipperErrorData(code: Int, text: String): FlipperError {

    }

    private fun getCurrentFlipperVersion(requestApi: FlipperRequestApi): SemVer {
        requestApi.
    }
}

enum class FlipperError(val id: Int) {
    ForbiddenFrequency(2),
    NotSupportedApp(0),
    BadResponse(-1)
}