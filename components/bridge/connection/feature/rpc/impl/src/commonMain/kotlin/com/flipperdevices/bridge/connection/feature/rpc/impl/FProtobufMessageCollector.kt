package com.flipperdevices.bridge.connection.feature.rpc.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.reader.PeripheralResponseReader
import com.flipperdevices.bridge.connection.feature.rpc.storage.FRequestStorage
import com.flipperdevices.bridge.connection.pbutils.encodeWithDelimitedSize
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.CommandStatus
import com.flipperdevices.protobuf.Main
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FProtobufMessageCollector @AssistedInject constructor(
    @Assisted scope: CoroutineScope,
    @Assisted restartApiFeature: FRestartRpcFeatureApi,
    @Assisted private val serialApi: FSerialDeviceApi,
    @Assisted private val requestListeners: MutableMap<Int, OnReceiveResponse>,
    @Assisted private val requestStorage: FRequestStorage,
    readerFactory: PeripheralResponseReader.Factory
) {
    private val reader: PeripheralResponseReader = readerFactory(
        scope = scope,
        restartRPCApi = restartApiFeature
    )
    private val notificationMutableFlow = MutableSharedFlow<Main>()
    val notificationFlow: Flow<Main> = notificationMutableFlow

    init {
        subscribeToAnswers(scope)
    }

    private fun subscribeToAnswers(scope: CoroutineScope) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            serialApi.getReceiveBytesFlow().collect {
                reader.onReceiveBytes(it)
            }
        }
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            reader.getResponses().collect {
                val listener = requestListeners[it.command_id]
                if (listener == null) {
                    warn { "Receive package without id $it" }
                    notificationMutableFlow.emit(it)
                } else {
                    listener.invoke(it)
                }
            }
        }

        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) { onClose() }
            }
        }
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            while (isActive) {
                val request = requestStorage.getNextRequest()
                if (request != null) {
                    serialApi.sendBytes(request.data.encodeWithDelimitedSize())
                    runCatching {
                        request.onSendCallback?.invoke()
                    }.onFailure {
                        error(it) { "Failed execute callback on send for $request" }
                    }
                }
            }
        }
    }

    private suspend fun onClose() {
        var counter = 0
        info { "Found ${requestListeners.size} request, start clean" }
        while (requestListeners.isNotEmpty()) {
            info { "Start iteration for clean ${requestListeners.size} requests" }
            val listeners = ArrayList(requestListeners.keys)
            listeners.forEach { id ->
                counter++
                val listener = requestListeners.remove(id)
                info { "Invoke close for $listener" }
                listener?.invoke(
                    Main(
                        command_status = CommandStatus.ERROR,
                        has_next = false
                    )
                )
            }
        }

        info { "Complete reset and finish $counter tasks" }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted scope: CoroutineScope,
            @Assisted restartApiFeature: FRestartRpcFeatureApi,
            @Assisted serialApi: FSerialDeviceApi,
            @Assisted requestListeners: MutableMap<Int, OnReceiveResponse>,
            @Assisted requestStorage: FRequestStorage,
        ): FProtobufMessageCollector
    }
}
