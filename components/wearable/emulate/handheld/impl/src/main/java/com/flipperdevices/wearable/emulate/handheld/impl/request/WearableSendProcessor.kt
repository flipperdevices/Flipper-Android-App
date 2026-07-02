package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.ForbiddenFrequencyException
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.EmulateStatus
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@Suppress("LongParameterList")
@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableSendProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<MainResponse>,
    private val scope: CoroutineScope,
    private val simpleKeyApi: SimpleKeyApi,
    private val keyParser: KeyParser,
    private val fFeatureProvider: FFeatureProvider
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearableSendProcessor-${hashCode()}"

    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            val sendRequest = it.send_request
            if (sendRequest != null) {
                info { "SendRequest: $it" }
                startSend(sendRequest.path)
            }
        }.launchIn(scope)
    }

    private suspend fun startSend(path: String) {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        info { "#sendEmulate $path" }

        val keyType = FlipperKeyType.getByExtension(File(path).extension) ?: return
        info { "keyType $keyType" }

        val keyPath = path.replaceFirstChar { if (it == '/') "" else it.toString() }
        val keyFile = File(keyPath)
        val filePath = FlipperFilePath(keyFile.parent.orEmpty(), keyFile.name)
        val timeout = calculateTimeout(filePath)
        try {
            val emulateConfig = EmulateConfig(
                keyPath = filePath,
                keyType = keyType,
                minEmulateTime = timeout
            )
            info { "Emulate Config $emulateConfig" }
            commandOutputStream.send(
                MainResponse(emulate_status = EmulateStatus.EMULATING)
            )

            emulateHelper.startEmulate(scope, emulateConfig)
            commandOutputStream.send(
                MainResponse(emulate_status = EmulateStatus.STOPPED)
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed start send $path" }

            val failedEmulateStatus: EmulateStatus = when (throwable) {
                is AlreadyOpenedAppException -> EmulateStatus.ALREADY_OPENED_APP
                is ForbiddenFrequencyException -> EmulateStatus.FORBIDDEN_FREQUENCY
                else -> EmulateStatus.FAILED
            }

            commandOutputStream.send(
                MainResponse(emulate_status = failedEmulateStatus)
            )
        } finally {
            withContext(NonCancellable) {
                emulateHelper.stopEmulate(scope)
            }
        }
    }

    private suspend fun calculateTimeout(filePath: FlipperFilePath): Long {
        val flipperKey = simpleKeyApi.getKey(FlipperKeyPath(filePath, deleted = false)) ?: return 0
        val parsedKey = keyParser.parseKey(flipperKey)
        if (parsedKey is FlipperKeyParsed.SubGhz) {
            return parsedKey.totalTimeMs ?: 0
        }
        return 0
    }
}
