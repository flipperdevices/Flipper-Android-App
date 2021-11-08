package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGattService
import android.util.SparseArray
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.impl.manager.PeripheralResponseReader
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.protobuf.toDelimitedBytes
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.copy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
    private val serialApi = FlipperSerialOverflowThrottler(serialApiUnsafe)

    init {
        subscribeToAnswers()
    }

    override fun notificationFlow(): Flow<Flipper.Main> {
        return notificationMutableFlow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun request(command: Flipper.Main): Flow<Flipper.Main> = channelFlow {
        verbose { "Request $command" }
        // Generate unique ID for each command
        val uniqueId = findEmptyId()
        val requestBytes = command.copy {
            commandId = uniqueId
        }.toDelimitedBytes()

        // Add answer listener to listeners
        requestListeners[uniqueId] = {
            scope.launch {
                send(it)
            }
            if (!it.hasNext) {
                requestListeners.remove(uniqueId)
            }
        }

        serialApi.sendBytes(requestBytes)

        awaitClose {
            requestListeners.remove(uniqueId)
        }
    }

    override suspend fun requestWithoutAnswer(vararg command: Flipper.Main) {
        val commandBytes = command.map { it.toDelimitedBytes().toTypedArray() }
            .toTypedArray().flatten()

        serialApi.sendBytes(commandBytes.toByteArray())
    }

    @ObsoleteCoroutinesApi
    private fun subscribeToAnswers() {
        val reader = PeripheralResponseReader(scope)
        scope.launch {
            serialApi.receiveBytesFlow().collect {
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
