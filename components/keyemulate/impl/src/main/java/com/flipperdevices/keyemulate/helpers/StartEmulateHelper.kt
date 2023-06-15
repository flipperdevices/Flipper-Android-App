package com.flipperdevices.keyemulate.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.exception.ForbiddenFrequencyException
import com.flipperdevices.keyemulate.model.FlipperAppError
import com.flipperdevices.protobuf.Flipper
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
    @Suppress("LongParameterList") // TODO emulate config
    suspend fun onStart(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        keyType: FlipperKeyType,
        keyPath: FlipperFilePath,
        minEmulateTime: Long,
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
        keyType: FlipperKeyType,
        keyPath: FlipperFilePath,
        minEmulateTime: Long,
        onStop: suspend () -> Unit,
        onResultTime: (Long) -> Unit,
    ): Boolean {
        val requestApi = serviceApi.requestApi
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
                        path = keyPath.getPathOnFlipper()
                    }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        if (appLoadFileResponse.commandStatus != Flipper.CommandStatus.OK) {
            error { "Failed start key with error $appLoadFileResponse" }
            return false
        }
        if (keyType != FlipperKeyType.SUB_GHZ) {
            info { "Skip execute button press: $appLoadFileResponse" }
            return true
        }
        info { "This is subghz, so start press button" }
        val appButtonPressResponse = requestApi.request(
            flowOf(
                main {
                    appButtonPressRequest = appButtonPressRequest {}
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        return processResultStart(appButtonPressResponse, onResultTime, minEmulateTime, serviceApi)
    }

    private suspend fun processResultStart(
        appButtonPressResponse: Flipper.Main,
        onResultTime: (Long) -> Unit,
        minEmulateTime: Long,
        serviceApi: FlipperServiceApi
    ): Boolean {
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
