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
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.copy
import com.flipperdevices.protobuf.main
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

private typealias OnReceiveResponse = suspend (Flipper.Main) -> Unit

@Suppress("TooManyFunctions")
class FlipperRequestApiImpl(
    private val scope: CoroutineScope,
    private val lagsDetector: FlipperLagsDetector
) : FlipperRequestApi,
    BluetoothGattServiceWrapper,
    LogTagProvider {
    override val TAG = "FlipperRequestApi"
    private var idCounter = AtomicInteger(1)
    private val requestListeners = ConcurrentHashMap<Int, OnReceiveResponse>()
    private val notificationMutableFlow = MutableSharedFlow<Flipper.Main>()

    private val serialApiUnsafe = FlipperSerialApiUnsafeImpl(scope, lagsDetector)
    private val requestStorage: FlipperRequestStorage = FlipperRequestStorageImpl()
    private val serialApi = FlipperSerialOverflowThrottler(serialApiUnsafe, scope, requestStorage)

    init {
        subscribeToAnswers()
    }

    override fun notificationFlow(): Flow<Flipper.Main> {
        return notificationMutableFlow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun request(
        command: FlipperRequest
    ): Flow<Flipper.Main> = lagsDetector.wrapPendingAction(
        channelFlow {
            verbose { "Pending commands count: ${requestListeners.size}. Request $command" }
            // Generate unique ID for each command
            val uniqueId = findEmptyId()
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
                requestListeners.remove(uniqueId)
            }
        }
    )

    override suspend fun request(
        commandFlow: Flow<FlipperRequest>
    ): Flipper.Main = lagsDetector.wrapPendingAction {
        verbose { "Pending commands count: ${requestListeners.size}. Request command flow" }
        // Generate unique ID for each command
        val uniqueId = findEmptyId()
        val commandAnswer = scope.async { awaitCommandAnswer(uniqueId) }

        commandFlow.onEach { request ->
            val requestWithId = request.copy(
                data = request.data.copy {
                    commandId = uniqueId
                }
            )
            requestWithoutAnswer(requestWithId)
        }.launchIn(scope)

        return@wrapPendingAction commandAnswer.await()
    }

    override suspend fun requestWithoutAnswer(vararg commands: FlipperRequest) {
        requestStorage.sendRequest(*commands)
    }

    override suspend fun getSpeed(): StateFlow<FlipperSerialSpeed> {
        return serialApiUnsafe.getSpeed()
    }

    private fun subscribeToAnswers() {
        val reader = PeripheralResponseReader(scope)
        scope.launch {
            serialApiUnsafe.receiveBytesFlow().collect {
                reader.onReceiveBytes(it)
            }
        }
        scope.launch {
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

    private fun findEmptyId(): Int {
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

        cont.invokeOnCancellation { requestListeners.remove(uniqueId) }
    }

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        return serialApiUnsafe.onServiceReceived(gatt) &&
            serialApi.onServiceReceived(gatt)
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        serialApiUnsafe.initialize(bleManager)
        serialApi.initialize(bleManager)
    }

    override suspend fun reset(bleManager: UnsafeBleManager) {
        // Remove all elements from request listeners
        requestListeners.values.forEach {
            it.invoke(
                main {
                    hasNext = false
                }
            )
        }
        serialApiUnsafe.reset(bleManager)
        serialApi.reset(bleManager)
    }
}
