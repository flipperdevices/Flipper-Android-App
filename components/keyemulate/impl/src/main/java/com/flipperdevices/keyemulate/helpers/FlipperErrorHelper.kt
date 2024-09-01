package com.flipperdevices.keyemulate.helpers

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyemulate.model.FlipperAppError
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.getErrorRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface FlipperAppErrorHelper {
    suspend fun requestError(
        serviceApi: FlipperServiceApi,
        priority: FlipperRequestPriority
    ): FlipperAppError
}

@ContributesBinding(AppGraph::class, FlipperAppErrorHelper::class)
class FlipperAppErrorHandlerImpl @Inject constructor() : FlipperAppErrorHelper {
    override suspend fun requestError(
        serviceApi: FlipperServiceApi,
        priority: FlipperRequestPriority
    ): FlipperAppError {
        val requestApi = serviceApi.requestApi

        if (!serviceApi.flipperVersionApi.isSupported(Constants.API_SUPPORTED_FLIPPER_ERROR)) {
            return FlipperAppError.NotSupportedApi
        }

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
                FlipperAppError.fromCode(errorCode, errorText)
            }
            else -> FlipperAppError.BadResponse
        }
    }
}
