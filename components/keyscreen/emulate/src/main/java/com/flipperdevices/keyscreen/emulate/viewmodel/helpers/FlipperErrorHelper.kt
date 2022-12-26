package com.flipperdevices.keyscreen.emulate.viewmodel.helpers

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.emulate.model.FlipperAppError
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.getErrorRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf

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

        val currentFlipperVersion = getCurrentFlipperVersion(serviceApi)
        if (
            currentFlipperVersion == null ||
            currentFlipperVersion < Constants.API_SUPPORTED_FLIPPER_ERROR
        ) {
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

    private fun getCurrentFlipperVersion(serviceApi: FlipperServiceApi): SemVer? {
        return serviceApi.flipperVersionApi.getVersionInformationFlow().value
    }
}
