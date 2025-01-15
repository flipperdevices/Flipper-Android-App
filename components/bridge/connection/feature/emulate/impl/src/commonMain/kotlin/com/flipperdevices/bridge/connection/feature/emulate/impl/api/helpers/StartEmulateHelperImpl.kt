package com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.ForbiddenFrequencyException
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.AppEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.FlipperAppErrorHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StartEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.bridge.connection.feature.emulate.api.model.FlipperAppError
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.exception.FRpcException
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.CommandStatus
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.app.AppButtonPressReleaseRequest
import com.flipperdevices.protobuf.app.AppButtonPressRequest
import com.flipperdevices.protobuf.app.AppLoadFileRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

private const val APP_RETRY_COUNT = 3
private const val APP_RETRY_SLEEP_TIME_MS = 1 * 1000L // 1 second
const val SUBGHZ_DEFAULT_TIMEOUT_MS = 500L
const val INFRARED_DEFAULT_TIMEOUT_MS = 500L

val API_SUPPORTED_INFRARED_EMULATE = SemVer(
    majorVersion = 0,
    minorVersion = 21
)
val API_SUPPORTED_INFRARED_PRESS_RELEASE = SemVer(
    majorVersion = 0,
    minorVersion = 25
)

class StartEmulateHelperImpl(
    private val appEmulateHelper: AppEmulateHelper,
    private val flipperAppErrorHelper: FlipperAppErrorHelper,
    private val fRpcFeatureApi: FRpcFeatureApi,
    private val fVersionFeatureApi: FVersionFeatureApi
) : StartEmulateHelper, LogTagProvider {

    override val TAG: String = "StartEmulateHelper"

    override suspend fun onStart(
        scope: CoroutineScope,
        config: EmulateConfig,
        onStop: suspend () -> Unit,
        onResultTime: (Long) -> Unit,
    ): Boolean {
        val keyType = config.keyType

        info { "startEmulateInternal" }

        var appOpen = tryOpenApp(scope, keyType)
        var retryCount = 0
        while (!appOpen && retryCount < APP_RETRY_COUNT) {
            info { "Failed open app first time, try $retryCount times" }
            retryCount++
            onStop()
            delay(APP_RETRY_SLEEP_TIME_MS)
            appOpen = tryOpenApp(scope, keyType)
        }
        if (!appOpen) {
            info { "Failed open app with $retryCount retry times" }
            return false
        }
        info { "App open successful" }

        val appLoadFileResponse = fRpcFeatureApi.requestOnce(
            Main(
                app_load_file_request = AppLoadFileRequest(
                    path = config.keyPath.getPathOnFlipper()
                )
            ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).onFailure {
            error(it) { "Failed start key with error ${config.keyPath.getPathOnFlipper()}" }
            return false
        }
        if (!isNeedButtonPress(config)) {
            info { "Skip execute button press: $appLoadFileResponse" }
            return true
        }

        val indexEmulateSupported = fVersionFeatureApi.isSupported(API_SUPPORTED_INFRARED_EMULATE)

        val isPressReleaseSupported =
            fVersionFeatureApi.isSupported(API_SUPPORTED_INFRARED_PRESS_RELEASE)

        info { "Support emulate by index: $indexEmulateSupported" }

        return processButtonPress(
            config = config,
            onResultTime = onResultTime,
            isIndexEmulateSupport = indexEmulateSupported,
            isPressReleaseSupported = isPressReleaseSupported
        )
    }

    private fun isNeedButtonPress(config: EmulateConfig): Boolean {
        return config.keyType == FlipperKeyType.SUB_GHZ ||
            config.keyType == FlipperKeyType.INFRARED
    }

    private suspend fun processButtonPress(
        config: EmulateConfig,
        isIndexEmulateSupport: Boolean,
        isPressReleaseSupported: Boolean,
        onResultTime: (Long) -> Unit,
    ): Boolean {
        val minEmulateTime = getMinEmulateTime(config)

        info { "This is ${config.keyType}, so start press button with time $minEmulateTime" }

        try {
            fRpcFeatureApi.requestOnce(
                if (config.isPressRelease && isPressReleaseSupported) {
                    Main(
                        app_button_press_release_request = getAppButtonPressReleaseRequest(
                            config,
                            isIndexEmulateSupport
                        )
                    )
                } else {
                    Main(
                        app_button_press_request = getAppButtonPressRequest(
                            config,
                            isIndexEmulateSupport
                        )
                    )
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            ).getOrThrow()
            onResultTime(TimeHelper.getNow() + minEmulateTime)
            return true
        } catch (e: FRpcException) {
            val flipperError = flipperAppErrorHelper.requestError()

            if (isForbiddenFrequencyError(flipperError, e.response.command_status)) {
                error { "Handle generic error on press button" }
                throw ForbiddenFrequencyException()
            }
            error { "Failed press key. error $flipperError" }
            return false
        } catch (e: Exception) {
            error(e) { "#processButtonPress Unhandled exception" }
            return false
        }
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

    private fun getAppButtonPressReleaseRequest(
        config: EmulateConfig,
        isIndexEmulateSupport: Boolean,
    ): AppButtonPressReleaseRequest {
        return when (config.keyType) {
            FlipperKeyType.INFRARED -> if (isIndexEmulateSupport) {
                val indexArgs = config.index ?: error("Index args is null")
                info { "#getAppButtonPressReleaseRequest by index with $config" }
                AppButtonPressReleaseRequest(
                    index = indexArgs
                )
            } else {
                val configArgs = config.args ?: error("Config args is null")
                info { "#getAppButtonPressReleaseRequest by args with $config" }
                AppButtonPressReleaseRequest(
                    args = configArgs
                )
            }

            else -> error("#getAppButtonPressReleaseRequest Unknown button press request with config $config")
        }
    }

    private fun getAppButtonPressRequest(
        config: EmulateConfig,
        isIndexEmulateSupport: Boolean,
    ): AppButtonPressRequest {
        return when (config.keyType) {
            FlipperKeyType.SUB_GHZ -> AppButtonPressRequest()
            FlipperKeyType.INFRARED -> if (isIndexEmulateSupport) {
                val indexArgs = config.index ?: error("Index args is null")
                info { "#getAppButtonPressRequest by index with $config" }
                AppButtonPressRequest(
                    index = indexArgs
                )
            } else {
                val configArgs = config.args ?: error("Config args is null")
                info { "#getAppButtonPressRequest by args with $config" }
                AppButtonPressRequest(
                    args = configArgs
                )
            }

            else -> error("Unknown button press request with config $config")
        }
    }

    private fun isForbiddenFrequencyError(
        error: FlipperAppError,
        status: CommandStatus
    ): Boolean {
        if (
            error == FlipperAppError.NotSupportedApi &&
            status == CommandStatus.ERROR_APP_CMD_ERROR
        ) {
            return true
        }
        return error == FlipperAppError.ForbiddenFrequency
    }

    @Throws(AlreadyOpenedAppException::class)
    private suspend fun tryOpenApp(
        scope: CoroutineScope,
        keyType: FlipperKeyType
    ): Boolean = appEmulateHelper.tryOpenApp(
        scope = scope,
        keyType = keyType
    )
}
