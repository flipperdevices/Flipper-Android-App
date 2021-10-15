package com.flipperdevices.bridge.impl.manager

import android.util.SparseArray
import androidx.core.util.set
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.FlipperSerialApi
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.copy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

private typealias OnReceiveResponse = (Flipper.Main) -> Unit

class FlipperRequestApiImpl(
    private val serialApi: FlipperSerialApi,
    private val scope: CoroutineScope
) : FlipperRequestApi {
    private var idCounter = 0
    private val requestListeners = SparseArray<OnReceiveResponse>()
    private val notificationMutableFlow = MutableSharedFlow<Flipper.Main>()

    override fun notificationFlow(): Flow<Flipper.Main> {
        return notificationMutableFlow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun request(command: Flipper.Main): Flow<Flipper.Main> = flow {
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
                emit(it)
            }
        }

        serialApi.sendBytes(requestBytes)
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
            reader.getResponses()
        }
    }

    private fun findEmptyId(): Int {
        do {
            if (idCounter == Int.MAX_VALUE) {
                idCounter = 0
            } else idCounter++
        } while (requestListeners[idCounter] != null)
        return idCounter
    }
}