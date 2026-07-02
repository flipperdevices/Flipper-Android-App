package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.AlreadyOpenedAppException
import com.flipperdevices.bridge.connection.feature.emulate.api.exception.ForbiddenFrequencyException
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import dev.zacsweers.metro.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.EmulateStatus
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@SingleIn(WearHandheldGraph::class)
@ContributesIntoSet(WearHandheldGraph::class, binding<WearableCommandProcessor>())
class WearableStartEmulateProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<MainResponse>,
    private val scope: CoroutineScope,
    private val fFeatureProvider: FFeatureProvider
) : WearableCommandProcessor, LogTagProvider {
    override val TAG = "WearableStartEmulateProcessor-${hashCode()}"

    override fun init() {
        info { "#init" }
        commandInputStream.getRequestsFlow().onEach {
            val startEmulate = it.start_emulate
            if (startEmulate != null) {
                info { "StartEmulate: $it" }
                startEmulate(startEmulate.path)
            }
        }.launchIn(scope)
    }

    private suspend fun startEmulate(path: String) {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        info { "#startEmulate $path" }
        val keyType = FlipperKeyType.getByExtension(File(path).extension) ?: return
        info { "Key type is $keyType" }
        val keyPath = path.replaceFirstChar { if (it == '/') "" else it.toString() }
        val keyFile = File(keyPath)
        try {
            val emulateConfig = EmulateConfig(
                keyType = keyType,
                keyPath = FlipperFilePath(keyFile.parent.orEmpty(), keyFile.name)
            )
            commandOutputStream.send(
                MainResponse(emulate_status = EmulateStatus.EMULATING)
            )
            emulateHelper.startEmulate(scope, emulateConfig)
        } catch (throwable: Throwable) {
            error(throwable) { "Failed start emulate $path" }

            val failedEmulateStatus: EmulateStatus = when (throwable) {
                is AlreadyOpenedAppException -> EmulateStatus.ALREADY_OPENED_APP
                is ForbiddenFrequencyException -> EmulateStatus.FORBIDDEN_FREQUENCY
                else -> EmulateStatus.FAILED
            }

            commandOutputStream.send(
                MainResponse(emulate_status = failedEmulateStatus)
            )
        }
    }
}
