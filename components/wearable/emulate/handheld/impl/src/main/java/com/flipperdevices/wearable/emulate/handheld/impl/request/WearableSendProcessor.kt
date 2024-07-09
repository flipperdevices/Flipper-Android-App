package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.exception.ForbiddenFrequencyException
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject

@Suppress("LongParameterList")
@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableSendProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    private val simpleKeyApi: SimpleKeyApi,
    private val keyParser: KeyParser
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearableSendProcessor-${hashCode()}"

    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasSendRequest()) {
                info { "SendRequest: $it" }
                startSend(serviceProvider.getServiceApi(), it.sendRequest.path)
            }
        }.launchIn(scope)
    }

    private suspend fun startSend(serviceApi: FlipperServiceApi, path: String) {
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
                mainResponse {
                    emulateStatus = Emulate.EmulateStatus.EMULATING
                }
            )
            emulateHelper.startEmulate(scope, serviceApi, emulateConfig)
            commandOutputStream.send(
                mainResponse {
                    emulateStatus = Emulate.EmulateStatus.STOPPED
                }
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed start send $path" }

            val failedEmulateStatus: Emulate.EmulateStatus = when (throwable) {
                is AlreadyOpenedAppException -> Emulate.EmulateStatus.ALREADY_OPENED_APP
                is ForbiddenFrequencyException -> Emulate.EmulateStatus.FORBIDDEN_FREQUENCY
                else -> Emulate.EmulateStatus.FAILED
            }

            commandOutputStream.send(
                mainResponse {
                    emulateStatus = failedEmulateStatus
                }
            )
        } finally {
            emulateHelper.stopEmulate(scope, serviceApi.requestApi)
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
