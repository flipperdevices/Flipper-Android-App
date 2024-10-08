package com.flipperdevices.bridge.impl.manager.service.request

import android.bluetooth.BluetoothGatt
import com.flipperdevices.bridge.api.di.FlipperBleServiceGraph
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.flipperdevices.bridge.api.manager.delegates.FlipperLagsDetector
import com.flipperdevices.bridge.api.manager.service.RestartRPCApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.bridge.impl.manager.PeripheralResponseReader
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.overflow.FlipperRequestStorage
import com.flipperdevices.bridge.impl.manager.overflow.FlipperRequestStorageImpl
import com.flipperdevices.bridge.impl.manager.overflow.FlipperSerialOverflowThrottler
import com.flipperdevices.bridge.impl.manager.service.BluetoothGattServiceWrapper
import com.flipperdevices.bridge.impl.utils.BridgeImplConfig.BLE_VLOG
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.di.provideDelegate
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
import com.flipperdevices.shake2report.api.Shake2ReportApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.plus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Provider

private typealias OnReceiveResponse = suspend (Flipper.Main) -> Unit

@Suppress("TooManyFunctions")
@SingleIn(FlipperBleServiceGraph::class)
class FlipperRequestApiImpl @Inject constructor(
    scopeProvider: Provider<CoroutineScope>,
    flipperActionNotifierProvider: Provider<FlipperActionNotifier>,
    lagsDetectorProvider: Provider<FlipperLagsDetector>,
    restartRPCApiProvider: Provider<RestartRPCApi>,
    sentryApiProvider: Provider<Shake2ReportApi>
) : FlipperRequestApi,
    BluetoothGattServiceWrapper,
    LogTagProvider {
    override val TAG = "FlipperRequestApi"

    private val scope by scopeProvider
    private val flipperActionNotifier by flipperActionNotifierProvider
    private val lagsDetector by lagsDetectorProvider
    private val restartRPCApi by restartRPCApiProvider
    private val sentryApi by sentryApiProvider

    // Start from 1 because 0 is default in protobuf
    private val idCounter = AtomicInteger(1)
    private val requestListeners = ConcurrentHashMap<Int, OnReceiveResponse>()
    private val notificationMutableFlow = MutableSharedFlow<Flipper.Main>()

    private val reader = PeripheralResponseReader(scope, sentryApi, restartRPCApi)
    private val serialApiUnsafe = FlipperSerialApiUnsafeImpl(scope, flipperActionNotifier)
    private val requestStorage: FlipperRequestStorage = FlipperRequestStorageImpl()
    private val serialApi = FlipperSerialOverflowThrottler(serialApiUnsafe, scope, requestStorage)

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
            if (BLE_VLOG) {
                verbose { "Pending commands count: ${requestListeners.size}. Request $command" }
            }
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
        if (BLE_VLOG) {
            verbose { "Pending commands count: ${requestListeners.size}. Request command flow" }
        }
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

    override fun getSpeed(): StateFlow<FlipperSerialSpeed> {
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
                } else {
                    return@updateAndGetSafe it + 1
                }
            }
        } while (requestListeners[counter] != null)
        return counter
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun awaitCommandAnswer(
        uniqueId: Int
    ): Flipper.Main = suspendCancellableCoroutine { cont ->
        requestListeners[uniqueId] = {
            requestListeners.remove(uniqueId)
            cont.resume(it) { cause, _, _ ->
                error(cause) { "Error on resume execution of $uniqueId command. Answer is $it" }
            }
        }

        cont.invokeOnCancellation {
            requestStorage.removeIf { request ->
                request.data.commandId == uniqueId
            }
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
        info { "Serial api unsafe reset done" }
        serialApi.reset(bleManager)
        info { "Serial api reset done" }
        reader.reset()
        info { "Reader reset done" }
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

    override fun sendTrashBytesAndBrokeSession() {
        serialApi.sendTrashBytesAndBrokeSession()
    }

    private fun subscribeToAnswers(scope: CoroutineScope) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            serialApiUnsafe.receiveBytesFlow().collect {
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
    }
}
