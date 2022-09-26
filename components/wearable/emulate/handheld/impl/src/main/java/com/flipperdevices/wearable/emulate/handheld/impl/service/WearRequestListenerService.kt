package com.flipperdevices.wearable.emulate.handheld.impl.service

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.protobuf.toDelimitedBytes
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
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.EmulateStatusOuterClass
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearServiceComponent
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
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

    private val channelClient by lazy { Wearable.getChannelClient(this) }

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

    private var currentChannel: ChannelClient.Channel? = null

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        super.onChannelOpened(channel)
        currentChannel = channel
        lifecycleScope.launch(Dispatchers.Default) {
            channelClient.getInputStream(channel).await().use {
                processRequest(MainRequest.parseDelimitedFrom(it))
            }
        }
    }

    override fun onChannelClosed(
        channel: ChannelClient.Channel,
        closeReason: Int,
        appSpecificErrorCode: Int
    ) {
        super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
        currentChannel = null
    }

    private suspend fun processRequest(ipcRequest: MainRequest) {
        val case = ipcRequest.contentCase ?: return
        when (case) {
            MainRequest.ContentCase.START_EMULATE -> withContext(Dispatchers.Main) {
                serviceProvider.provideServiceApi(this@WearRequestListenerService) { serviceApi ->
                    val keyPath =
                        ipcRequest.startEmulate.path.replaceFirstChar { if (it == '/') "" else it.toString() }
                    val keyFile = File(keyPath)
                    val filePath = FlipperFilePath(keyFile.parent ?: "", keyFile.name)
                    lifecycleScope.launch {
                        startEmulate(serviceApi.requestApi, filePath, short = false)
                    }
                }
            }
            MainRequest.ContentCase.CONTENT_NOT_SET -> return
        }
    }

    private suspend fun startEmulate(
        requestApi: FlipperRequestApi,
        filePath: FlipperFilePath,
        short: Boolean
    ): Unit = withContext(Dispatchers.Default) {
        val keyType = filePath.keyType ?: return@withContext

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
                emulateHelper.stopEmulate(lifecycleScope, requestApi)
            }
        } catch (throwable: Exception) {
            error(throwable) { "Error while emulate $filePath" }
            sendClose()
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

    private suspend fun sendClose() = withContext(Dispatchers.Default) {
        currentChannel?.let { notNullableChannel ->
            channelClient.getOutputStream(notNullableChannel).await().use {
                it.write(mainResponse {
                    emulateStatus = EmulateStatusOuterClass.EmulateStatus.STOP_EMULATE
                }.toDelimitedBytes())
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.notificationFlow().onEach { unknownMessage ->
            if (unknownMessage.hasAppStateResponse()) {
                if (unknownMessage.appStateResponse.state == Application.AppState.APP_CLOSED) {
                    sendClose()
                }
            }
        }.launchIn(lifecycleScope)
    }
}
