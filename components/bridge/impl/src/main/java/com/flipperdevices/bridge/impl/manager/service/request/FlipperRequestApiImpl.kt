package com.flipperdevices.bridge.impl.manager.service.request

import android.bluetooth.BluetoothGatt
import com.flipperdevices.bridge.api.manager.FlipperLagsDetector
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.bridge.impl.manager.PeripheralResponseReader
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.overflow.FlipperRequestStorage
import com.flipperdevices.bridge.impl.manager.overflow.FlipperRequestStorageImpl
import com.flipperdevices.bridge.impl.manager.overflow.FlipperSerialOverflowThrottler
import com.flipperdevices.bridge.impl.manager.service.BluetoothGattServiceWrapper
import com.flipperdevices.core.ktx.jre.updateAndGetSafe
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.copy
import com.flipperdevices.protobuf.main
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

private typealias OnReceiveResponse = suspend (Flipper.Main) -> Unit

@Suppress("TooManyFunctions")
class FlipperRequestApiImpl(
    private val scope: CoroutineScope,
    private val lagsDetector: FlipperLagsDetector
) : FlipperRequestApi,
    BluetoothGattServiceWrapper,
    LogTagProvider {
    override val TAG = "FlipperRequestApi"

    // Start from 1 because 0 is default in protobuf
    private var idCounter = AtomicInteger(1)
    private val requestListeners = ConcurrentHashMap<Int, OnReceiveResponse>()
    private val notificationMutableFlow = MutableSharedFlow<Flipper.Main>()

    private val reader = PeripheralResponseReader(scope)
    private val serialApiUnsafe = FlipperSerialApiUnsafeImpl(scope, lagsDetector)
    private val requestStorage: FlipperRequestStorage = FlipperRequestStorageImpl()
    private val serialApi = FlipperSerialOverflowThrottler(serialApiUnsafe, scope, requestStorage)

    init {
        subscribeToAnswers(scope)
    }

    override fun notificationFlow(): Flow<Flipper.Main> {
        return notificationMutableFlow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
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
        }.launchIn(scope)

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

    override suspend fun getSpeed(): StateFlow<FlipperSerialSpeed> {
        return serialApiUnsafe.getSpeed()
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
                } else return@updateAndGetSafe it + 1
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

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        val serialApiUnsafe = serialApiUnsafe.onServiceReceived(gatt)
        val serialApi = serialApi.onServiceReceived(gatt)

        return serialApiUnsafe && serialApi
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        serialApiUnsafe.initialize(bleManager)
        serialApi.initialize(bleManager)
        reader.initialize()
    }

    override suspend fun reset(bleManager: UnsafeBleManager) {
        // Remove all elements from request listeners
        serialApiUnsafe.reset(bleManager)
        serialApi.reset(bleManager)
        reader.reset()
        var counter = 0
        while (requestListeners.isNotEmpty()) {
            val listeners = ArrayList(requestListeners.keys)
            listeners.forEach { id ->
                counter++
                val listener = requestListeners.remove(id)
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
        scope.launch(Dispatchers.Default) {
            serialApiUnsafe.receiveBytesFlow().collect {
                reader.onReceiveBytes(it)
            }
        }
        scope.launch(Dispatchers.Default) {
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
    }
}
