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
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.ifrmvp.backend.model.SignalModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class SaveSignalViewModel(
    private val context: Context,
    private val serviceProvider: FlipperServiceProvider
) : DecomposeViewModel(), FlipperBleServiceConsumer, LogTagProvider {
    private val mutex = Mutex()
    override val TAG: String = "SaveFileViewModel"
    val state = MutableStateFlow<State>(State.Pending)

    private fun SignalModel.toFFF() = FlipperFileFormat(
        orderedDict = listOf(
            ("Filetype" to "IR signals file"),
            ("Version" to "1"),
            ("name" to name),
            ("type" to type),
            ("frequency" to frequency),
            ("duty_cycle" to dutyCycle),
            ("data" to data),
            ("protocol" to protocol),
            ("address" to address),
            ("command" to command),
        ).mapNotNull { (k, v) -> if (v == null) null else k to v }
    )

    fun reset() {
        state.value = State.Pending
    }

    fun save(signalModel: SignalModel) = viewModelScope.launch(Dispatchers.Main) {
        val fff = signalModel.toFFF()
        val deeplinkContent = DeeplinkContent.FFFContent("ifr_temp.ir", fff)
        val ffPath = FlipperFilePath(
            FlipperKeyType.INFRARED.flipperDir,
            "ifr_temp.ir"
        )
        val contentResolver = context.contentResolver
        val messageSize = fff.length()
        state.value = State.Uploading(0, messageSize)
        serviceProvider.provideServiceApi(
            lifecycleOwner = this@SaveSignalViewModel,
            onError = { state.value = State.Error }
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
                                        is State.Uploading -> {
                                            val progressInternal = state.progressInternal + size
                                            state.copy(progressInternal = progressInternal)
                                        }

                                        else -> State.Uploading(size, messageSize)
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
                                state.value = State.Uploaded
                            }
                        )
                        info { "File send with response $response" }
                    }
                    state.value = State.Uploaded
                }
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit

    sealed interface State {
        data object Pending : State
        data object Error : State
        data class Uploading(val progressInternal: Long, val total: Long) : State {
            val progress: Float = if (total == 0L) 0f else progressInternal / total.toFloat()
        }

        data object Uploaded : State
    }
}
