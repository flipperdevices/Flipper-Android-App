package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.model.EmulateConfig
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

@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableStartEmulateProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper
) : WearableCommandProcessor, LogTagProvider {
    override val TAG = "WearableStartEmulateProcessor"

    override fun init() {
        info { "#init" }
        commandInputStream.getRequestsFlow().onEach {
            info { "found request $it" }
            if (it.hasStartEmulate()) {
                info { "found start request $it" }
                startEmulate(serviceProvider.getServiceApi(), it.startEmulate.path)
            }
        }.launchIn(scope)
    }

    private suspend fun startEmulate(serviceApi: FlipperServiceApi, path: String) {
        info { "#startEmulate $path" }
        val keyType = FlipperKeyType.getByExtension(File(path).extension) ?: return
        commandOutputStream.send(
            mainResponse {
                emulateStatus = Emulate.EmulateStatus.EMULATING
            }
        )
        info { "Key type is $keyType" }
        val keyPath = path.replaceFirstChar { if (it == '/') "" else it.toString() }
        val keyFile = File(keyPath)
        try {
            val emulateConfig = EmulateConfig(
                keyType = keyType,
                keyPath = FlipperFilePath(keyFile.parent ?: "", keyFile.name)
            )
            emulateHelper.startEmulate(scope, serviceApi, emulateConfig)
            commandOutputStream.send(
                mainResponse {
                    emulateStatus = Emulate.EmulateStatus.EMULATING
                }
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed start emulate $path" }
            commandOutputStream.send(
                mainResponse {
                    emulateStatus = Emulate.EmulateStatus.FAILED
                }
            )
        }
    }
}
