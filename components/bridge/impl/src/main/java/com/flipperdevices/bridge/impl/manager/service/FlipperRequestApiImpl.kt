package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGattService
import android.util.SparseArray
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.impl.manager.PeripheralResponseReader
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.overflow.FlipperRequestStorage
import com.flipperdevices.bridge.impl.manager.overflow.FlipperRequestStorageImpl
import com.flipperdevices.bridge.impl.manager.overflow.FlipperSerialOverflowThrottler
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.copy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

private typealias OnReceiveResponse = (Flipper.Main) -> Unit

class FlipperRequestApiImpl(
    private val scope: CoroutineScope
) : FlipperRequestApi,
    BluetoothGattServiceWrapper,
    LogTagProvider {
    override val TAG = "FlipperRequestApi"
    private var idCounter = 1
    private val requestListeners = SparseArray<OnReceiveResponse>()
    private val notificationMutableFlow = MutableSharedFlow<Flipper.Main>()

    private val serialApiUnsafe = FlipperSerialApiImpl(scope)
    private val requestStorage: FlipperRequestStorage = FlipperRequestStorageImpl()
    private val serialApi = FlipperSerialOverflowThrottler(serialApiUnsafe, scope, requestStorage)

    init {
        subscribeToAnswers()
    }

    override fun notificationFlow(): Flow<Flipper.Main> {
        return notificationMutableFlow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun request(command: FlipperRequest): Flow<Flipper.Main> = channelFlow {
        verbose { "Request $command" }
        // Generate unique ID for each command
        val uniqueId = findEmptyId()
        val requestWithId = command.copy(
            data = command.data.copy {
                commandId = uniqueId
            }
        )

        // Add answer listener to listeners
        requestListeners[uniqueId] = {
            scope.launch {
                send(it)
            }
            if (!it.hasNext) {
                requestListeners.remove(uniqueId)
            }
        }

        requestStorage.sendRequest(requestWithId)

        awaitClose {
            requestListeners.remove(uniqueId)
        }
    }

    override suspend fun request(commandFlow: Flow<FlipperRequest>): Flipper.Main {
        verbose { "Request command flow" }
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

        return commandAnswer.await()
    }

    override suspend fun requestWithoutAnswer(vararg commands: FlipperRequest) {
        requestStorage.sendRequest(*commands)
    }

    @ObsoleteCoroutinesApi
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
        do {
            if (idCounter == Int.MAX_VALUE) {
                idCounter = 1
            } else idCounter++
        } while (requestListeners[idCounter] != null)
        return idCounter
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

    override fun onServiceReceived(service: BluetoothGattService) {
        serialApiUnsafe.onServiceReceived(service)
        serialApi.onServiceReceived(service)
    }

    override fun initialize(bleManager: UnsafeBleManager) {
        serialApiUnsafe.initialize(bleManager)
        serialApi.initialize(bleManager)
    }

    override fun reset(bleManager: UnsafeBleManager) {
        serialApiUnsafe.reset(bleManager)
        serialApi.reset(bleManager)
    }
}
