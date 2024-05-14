package com.flipperdevices.bridge.connection.feature.rpc.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.rpc.reader.PeripheralResponseReader
import com.flipperdevices.bridge.connection.feature.rpc.storage.FRequestStorage
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.protobuf.toDelimitedBytes
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.updateAndGetSafe
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.copy
import com.flipperdevices.protobuf.main
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private typealias OnReceiveResponse = suspend (Flipper.Main) -> Unit

class FRpcFeatureApiImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val serialApi: FSerialDeviceApi,
    @Assisted private val lagsDetector: FLagsDetectorFeature,
    @Assisted private val restartApiFeature: FRestartRpcFeatureApi,
    private val readerFactory: PeripheralResponseReader.Factory
) : FRpcFeatureApi, LogTagProvider {
    override val TAG = "FlipperRequestApi"

    // Start from 1 because 0 is default in protobuf
    private var idCounter = AtomicInteger(1)
    private val requestListeners = ConcurrentHashMap<Int, OnReceiveResponse>()
    private val notificationMutableFlow = MutableSharedFlow<Flipper.Main>()
    private val requestStorage: FRequestStorage = FRequestStorage()
    private val reader = readerFactory(
        scope = scope,
        restartRPCApi = restartApiFeature
    )

    init {
        subscribeToAnswers(scope)
    }

    override fun notificationFlow(): Flow<Flipper.Main> {
        return notificationMutableFlow
    }

    override fun request(
        command: FlipperRequest
    ): Flow<Flipper.Main> = lagsDetector.wrapPendingAction(
        command,
        channelFlow {
            verbose { "Pending commands count: ${requestListeners.size}. Request $command" }
            // Generate unique ID for each command
            val uniqueId = findEmptyId(currentId = command.data.commandId)
            val requestWithId = command.copy(
                data = command.data.copy {
                    commandId = uniqueId
                }
            )

            // Add answer listener to listeners
            requestListeners[uniqueId] = {
                send(it)
                if (!it.hasNext) {
                    requestListeners.remove(uniqueId)
                    close()
                }
            }

            requestStorage.sendRequest(requestWithId)

            awaitClose {
                requestStorage.removeRequest(requestWithId)
                requestListeners.remove(uniqueId)
            }
        }
    )

    override suspend fun request(
        commandFlow: Flow<FlipperRequest>,
        onCancel: suspend (Int) -> Unit
    ): Flipper.Main = lagsDetector.wrapPendingAction(null) {
        verbose { "Pending commands count: ${requestListeners.size}. Request command flow" }

        // Generate unique ID for each command
        val uniqueId = findEmptyId()
        // This is dirty way to understand if request is finished correctly with response
        var isFinished = false

        @Suppress("SuspendFunctionOnCoroutineScope")
        val commandAnswerJob = scope.async {
            val result = awaitCommandAnswer(uniqueId)
            isFinished = true
            return@async result
        }

        val flowCollectJob = commandFlow.onEach { request ->
            val requestWithId = request.copy(
                data = request.data.copy {
                    commandId = uniqueId
                }
            )
            requestStorage.sendRequest(requestWithId)
        }.onCompletion {
            if (it != null) {
                error(it) { "Cancel send because flow is failed" }
                commandAnswerJob.cancelAndJoin()
            }
        }.launchIn(scope + FlipperDispatchers.workStealingDispatcher)

        return@wrapPendingAction try {
            commandAnswerJob.await()
        } finally {
            withContext(NonCancellable) {
                flowCollectJob.cancelAndJoin()
                commandAnswerJob.cancelAndJoin()
                if (!isFinished) {
                    info { "Requests with flow with id $uniqueId is canceled" }
                    onCancel(uniqueId)
                }
            }
        }
    }

    override suspend fun requestWithoutAnswer(vararg commands: FlipperRequest) {
        requestStorage.sendRequest(*commands)
    }

    private fun findEmptyId(currentId: Int = 0): Int {
        if (currentId != 0 && requestListeners[currentId] == null) {
            return currentId
        }

        var counter: Int
        do {
            counter = idCounter.updateAndGetSafe {
                if (it == Int.MAX_VALUE) {
                    return@updateAndGetSafe 1
                } else {
                    return@updateAndGetSafe it + 1
                }
            }
        } while (requestListeners[counter] != null)
        return counter
    }

    private suspend fun awaitCommandAnswer(
        uniqueId: Int
    ): Flipper.Main = suspendCancellableCoroutine { cont ->
        requestListeners[uniqueId] = {
            requestListeners.remove(uniqueId)
            cont.resume(it) { throwable ->
                error(throwable) { "Error on resume execution of $uniqueId command. Answer is $it" }
            }
        }

        cont.invokeOnCancellation {
            requestStorage.removeIf { it.data.commandId == uniqueId }
            requestListeners.remove(uniqueId)
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
                    main {
                        commandStatus = Flipper.CommandStatus.ERROR
                        hasNext = false
                    }
                )
            }
        }

        info { "Complete reset and finish $counter tasks" }
    }

    private fun subscribeToAnswers(scope: CoroutineScope) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            serialApi.getReceiveBytesFlow().collect {
                reader.onReceiveBytes(it)
            }
        }
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            reader.getResponses().collect {
                val listener = requestListeners[it.commandId]
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
                    serialApi.sendBytes(request.data.toDelimitedBytes())
                }
            }
        }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            scope: CoroutineScope,
            serialApi: FSerialDeviceApi,
            lagsDetector: FLagsDetectorFeature,
            restartApiFeature: FRestartRpcFeatureApi
        ): FRpcFeatureApiImpl
    }
}
