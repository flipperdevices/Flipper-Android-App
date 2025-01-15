package com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.FlipperAppErrorHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.model.FlipperAppError
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.app.GetErrorRequest

private val API_SUPPORTED_FLIPPER_ERROR = SemVer(
    majorVersion = 0,
    minorVersion = 14
)

class FlipperAppErrorHandlerImpl(
    private val fRpcFeatureApi: FRpcFeatureApi,
    private val fVersionFeatureApi: FVersionFeatureApi
) : FlipperAppErrorHelper {
    override suspend fun requestError(): FlipperAppError {
        if (!fVersionFeatureApi.isSupported(API_SUPPORTED_FLIPPER_ERROR)) {
            return FlipperAppError.NotSupportedApi
        }

        try {
            val errorResponse = fRpcFeatureApi.requestOnce(
                Main(
                    app_get_error_request = GetErrorRequest()
                ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
            ).getOrThrow()
            val errorCode = errorResponse.app_get_error_response
                ?.code
                ?: return FlipperAppError.BadResponse
            val errorText = errorResponse.app_get_error_response
                ?.text
                ?: return FlipperAppError.BadResponse
            return FlipperAppError.fromCode(errorCode, errorText)
        } catch (e: Exception) {
            return FlipperAppError.BadResponse
        }
    }
}
