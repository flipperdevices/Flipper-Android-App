package com.flipperdevices.keyemulate.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.api.INFRARED_DEFAULT_TIMEOUT_MS
import com.flipperdevices.keyemulate.api.SUBGHZ_DEFAULT_TIMEOUT_MS
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.exception.ForbiddenFrequencyException
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.model.FlipperAppError
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.protobuf.app.appButtonPressRequest
import com.flipperdevices.protobuf.app.appLoadFileRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

private const val APP_RETRY_COUNT = 3
private const val APP_RETRY_SLEEP_TIME_MS = 1 * 1000L // 1 second

interface StartEmulateHelper {
    suspend fun onStart(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig,
        onStop: suspend () -> Unit,
        onResultTime: (Long) -> Unit
    ): Boolean
}

@ContributesBinding(AppGraph::class, StartEmulateHelper::class)
class StartEmulateHelperImpl @Inject constructor(
    private val appEmulateHelper: AppEmulateHelper,
    private val flipperAppErrorHelper: FlipperAppErrorHelper
) : StartEmulateHelper, LogTagProvider {

    override val TAG: String = "StartEmulateHelper"

    override suspend fun onStart(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig,
        onStop: suspend () -> Unit,
        onResultTime: (Long) -> Unit,
    ): Boolean {
        val requestApi = serviceApi.requestApi
        val keyType = config.keyType

        info { "startEmulateInternal" }

        var appOpen = tryOpenApp(scope, requestApi, keyType)
        var retryCount = 0
        while (!appOpen && retryCount < APP_RETRY_COUNT) {
            info { "Failed open app first time, try $retryCount times" }
            retryCount++
            onStop()
            delay(APP_RETRY_SLEEP_TIME_MS)
            appOpen = tryOpenApp(scope, requestApi, keyType)
        }
        if (!appOpen) {
            info { "Failed open app with $retryCount retry times" }
            return false
        }
        info { "App open successful" }

        val appLoadFileResponse = requestApi.request(
            flowOf(
                main {
                    appLoadFileRequest = appLoadFileRequest {
                        path = config.keyPath.getPathOnFlipper()
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        if (appLoadFileResponse.commandStatus != Flipper.CommandStatus.OK) {
            error { "Failed start key with error $appLoadFileResponse" }
            return false
        }
        if (!isNeedButtonPress(config)) {
            info { "Skip execute button press: $appLoadFileResponse" }
            return true
        }

        return processButtonPress(
            config = config,
            onResultTime = onResultTime,
            serviceApi = serviceApi
        )
    }

    private fun isNeedButtonPress(config: EmulateConfig): Boolean {
        return config.keyType == FlipperKeyType.SUB_GHZ ||
            config.keyType == FlipperKeyType.INFRARED
    }

    private suspend fun processButtonPress(
        config: EmulateConfig,
        onResultTime: (Long) -> Unit,
        serviceApi: FlipperServiceApi
    ): Boolean {
        val minEmulateTime = getMinEmulateTime(config)

        info { "This is ${config.keyType}, so start press button with time $minEmulateTime" }

        val appButtonPressResponse = serviceApi.requestApi.request(
            flowOf(
                main {
                    appButtonPressRequest = getAppButtonPressRequest(config)
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )

        val responseStatus = appButtonPressResponse.commandStatus

        if (responseStatus == Flipper.CommandStatus.OK) {
            onResultTime(TimeHelper.getNow() + minEmulateTime)
            return true
        }

        val flipperError = flipperAppErrorHelper.requestError(
            serviceApi = serviceApi,
            priority = FlipperRequestPriority.FOREGROUND
        )

        if (isForbiddenFrequencyError(flipperError, responseStatus)) {
            error { "Handle generic error on press button" }
            throw ForbiddenFrequencyException()
        }

        error { "Failed press key $appButtonPressResponse error $flipperError" }
        return false
    }

    private fun getMinEmulateTime(config: EmulateConfig): Long {
        val configMinEmulateTime = config.minEmulateTime
        if (configMinEmulateTime != null) {
            return configMinEmulateTime
        }

        return when (config.keyType) {
            FlipperKeyType.SUB_GHZ -> SUBGHZ_DEFAULT_TIMEOUT_MS
            FlipperKeyType.INFRARED -> INFRARED_DEFAULT_TIMEOUT_MS
            else -> error("Unknown key type for get min emulate time ${config.keyType}")
        }
    }

    private fun getAppButtonPressRequest(config: EmulateConfig): Application.AppButtonPressRequest {
        val configArgs = config.args
        val keyType = config.keyType

        return when {
            keyType == FlipperKeyType.SUB_GHZ -> appButtonPressRequest {}
            configArgs != null && keyType == FlipperKeyType.INFRARED -> appButtonPressRequest {
                args = configArgs
            }
            else -> error("Unknown button press request with config $config")
        }
    }

    private fun isForbiddenFrequencyError(
        error: FlipperAppError,
        status: Flipper.CommandStatus
    ): Boolean {
        if (
            error == FlipperAppError.NotSupportedApi &&
            status == Flipper.CommandStatus.ERROR_APP_CMD_ERROR
        ) {
            return true
        }
        return error == FlipperAppError.ForbiddenFrequency
    }

    @Throws(AlreadyOpenedAppException::class)
    private suspend fun tryOpenApp(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        keyType: FlipperKeyType
    ): Boolean = appEmulateHelper.tryOpenApp(
        scope = scope,
        requestApi = requestApi,
        keyType = keyType
    )
}
