package com.flipperdevices.wearable.emulate.handheld.impl.service

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyscreen.api.EmulateHelper
import com.flipperdevices.keyscreen.api.SUBGHZ_DEFAULT_TIMEOUT_MS
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.wearable.emulate.common.WearEmulateConstants.MESSAGE_PATH_EMULATE
import com.flipperdevices.wearable.emulate.common.WearEmulateConstants.MESSAGE_PATH_EMULATE_CLOSE
import com.flipperdevices.wearable.emulate.common.WearEmulateConstants.MESSAGE_PATH_EMULATE_SHORT
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearServiceComponent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.nio.charset.Charset
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WearRequestListenerService :
    LifecycleWearableListenerService(),
    LogTagProvider,
    FlipperBleServiceConsumer {
    override val TAG = "WearRequestListenerService"

    private val messageClient by lazy { Wearable.getMessageClient(this) }

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var emulateHelper: EmulateHelper

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var parser: KeyParser

    init {
        ComponentHolder.component<WearServiceComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    private var lastNode: String? = null

    override fun onMessageReceived(message: MessageEvent) {
        super.onMessageReceived(message)
        info { "Receive message $message" }

        val keyPath = String(message.data, Charset.forName("UTF-8"))
            .replaceFirstChar { if (it == '/') "" else it.toString() }
        val keyFile = File(keyPath)
        val filePath = FlipperFilePath(keyFile.parent ?: "", keyFile.name)

        if (filePath.keyType == null) {
            info { "Can't find type for key, send close $filePath" }
            sendClose(message.sourceNodeId)
            return
        }

        lastNode = message.sourceNodeId
        lifecycleScope.launch {
            processMessage(message, filePath)
        }
    }

    private suspend fun processMessage(
        message: MessageEvent,
        filePath: FlipperFilePath
    ) = withContext(Dispatchers.Main) {
        serviceProvider.provideServiceApi(this@WearRequestListenerService) { serviceApi ->
            lifecycleScope.launch {
                when (message.path) {
                    MESSAGE_PATH_EMULATE -> startEmulate(
                        requestApi = serviceApi.requestApi,
                        filePath = filePath,
                        short = false
                    )
                    MESSAGE_PATH_EMULATE_SHORT -> startEmulate(
                        requestApi = serviceApi.requestApi,
                        filePath = filePath,
                        short = true
                    )
                    MESSAGE_PATH_EMULATE_CLOSE -> emulateHelper.stopEmulateForce(
                        serviceApi.requestApi
                    )
                    else -> error { "Can't found task for ${message.path}" }
                }
            }
        }
    }

    private suspend fun startEmulate(
        requestApi: FlipperRequestApi,
        filePath: FlipperFilePath,
        short: Boolean
    ) {
        val keyType = filePath.keyType ?: return

        val timeout = if (keyType == FlipperKeyType.SUB_GHZ) {
            calculateTimeout(filePath) ?: SUBGHZ_DEFAULT_TIMEOUT_MS
        } else 0L

        try {
            emulateHelper.startEmulate(
                scope = lifecycleScope,
                requestApi = requestApi,
                keyType = keyType,
                keyPath = filePath,
                minEmulateTime = timeout
            )
            if (short) {
                emulateHelper.stopEmulate(GlobalScope, requestApi)
            }
        } catch (throwable: Exception) {
            error(throwable) { "Error while emulate $filePath" }
            lastNode?.let { sendClose(it) }
        }
    }

    private suspend fun calculateTimeout(filePath: FlipperFilePath): Long? =
        withContext(Dispatchers.Default) {
            val flipperKey = simpleKeyApi.getKey(FlipperKeyPath(filePath, deleted = false))
            if (flipperKey == null) {
                info { "Skip calculate timeout because not found $filePath" }
                return@withContext null
            }
            val parsedKey = parser.parseKey(flipperKey)
            return@withContext (parsedKey as? FlipperKeyParsed.SubGhz)?.totalTimeMs
        }

    private fun sendClose(nodeId: String) {
        lifecycleScope.launch {
            messageClient.sendMessage(
                nodeId,
                MESSAGE_PATH_EMULATE_CLOSE,
                byteArrayOf()
            ).await()
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.notificationFlow().onEach { unknownMessage ->
            if (unknownMessage.hasAppStateResponse()) {
                if (unknownMessage.appStateResponse.state == Application.AppState.APP_CLOSED) {
                    lastNode?.let { sendClose(it) }
                }
            }
        }.launchIn(lifecycleScope)
    }
}
