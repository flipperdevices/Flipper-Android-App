package com.flipperdevices.bridge.impl.manager

import android.util.SparseArray
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.service.FlipperSerialApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.copy
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private typealias OnReceiveResponse = (Flipper.Main) -> Unit

class FlipperRequestApiImpl(
    private val serialApi: FlipperSerialApi,
    private val scope: CoroutineScope
) : FlipperRequestApi, LogTagProvider {
    override val TAG = "FlipperRequestApi"
    private var idCounter = 1
    private val requestListeners = SparseArray<OnReceiveResponse>()
    private val notificationMutableFlow = MutableSharedFlow<Flipper.Main>(replay = 1)

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
        val requestBytes = withContext(Dispatchers.IO) { // Launch in IO dispatcher
            ByteArrayOutputStream().use { os ->
                command.copy {
                    commandId = uniqueId
                }.writeDelimitedTo(os)
                return@use os.toByteArray()
            }
        }

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
}
