package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import android.content.Context
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.protobuf.streamToCommandFlow
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.remotecontrols.api.SaveSignalApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@ContributesBinding(AppGraph::class, SaveSignalApi::class)
class SaveSignalViewModel @Inject constructor(
    private val context: Context,
    private val serviceProvider: FlipperServiceProvider
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider,
    SaveSignalApi {
    private val mutex = Mutex()
    override val TAG: String = "SaveFileViewModel"
    override val state = MutableStateFlow<SaveSignalApi.State>(SaveSignalApi.State.Pending)

    override fun save(fff: FlipperFileFormat, filePath: String) {
        val deeplinkContent = DeeplinkContent.FFFContent(filePath, fff)
        val ffPath = FlipperFilePath(
            FlipperKeyType.INFRARED.flipperDir,
            filePath
        )
        val contentResolver = context.contentResolver
        val messageSize = fff.length()
        state.value = SaveSignalApi.State.Uploading(0, messageSize)
        serviceProvider.provideServiceApi(
            lifecycleOwner = this@SaveSignalViewModel,
            onError = { state.value = SaveSignalApi.State.Error }
        ) { serviceApi ->
            launchWithLock(mutex, viewModelScope, "load") {
                val requestApi = serviceApi.requestApi
                withContext(FlipperDispatchers.workStealingDispatcher) {
                    deeplinkContent.openStream(contentResolver).use { fileStream ->
                        val stream = fileStream ?: return@use
                        val requestFlow = streamToCommandFlow(
                            stream,
                            deeplinkContent.length()
                        ) { chunkData ->
                            storageWriteRequest = writeRequest {
                                path = ffPath.getPathOnFlipper()
                                file = file { data = chunkData }
                            }
                        }.map { message ->
                            FlipperRequest(
                                data = message,
                                priority = FlipperRequestPriority.FOREGROUND,
                                onSendCallback = {
                                    val size = message.storageWriteRequest.file.data.size().toLong()
                                    state.value = when (val state = state.value) {
                                        is SaveSignalApi.State.Uploading -> {
                                            val progressInternal = state.progressInternal + size
                                            state.copy(progressInternal = progressInternal)
                                        }

                                        else -> SaveSignalApi.State.Uploading(size, messageSize)
                                    }
                                }
                            )
                        }
                        val response = requestApi.request(
                            commandFlow = requestFlow,
                            onCancel = { id ->
                                requestApi.request(
                                    main {
                                        commandId = id
                                        hasNext = false
                                        storageWriteRequest = writeRequest {
                                            path = ffPath.getPathOnFlipper()
                                        }
                                    }.wrapToRequest(FlipperRequestPriority.RIGHT_NOW)
                                ).collect()
                                state.value = SaveSignalApi.State.Uploaded
                            }
                        )
                        info { "File send with response $response" }
                    }
                    state.value = SaveSignalApi.State.Uploaded
                }
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit
}
