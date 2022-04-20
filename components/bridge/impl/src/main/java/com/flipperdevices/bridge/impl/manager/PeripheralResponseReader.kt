package com.flipperdevices.bridge.impl.manager

import com.flipperdevices.bridge.impl.utils.ByteEndlessInputStream
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

const val TAG = "PeripheralResponseReader"

@Suppress("BlockingMethodInNonBlockingContext")
class PeripheralResponseReader(
    private val scope: CoroutineScope
) : LogTagProvider {
    override val TAG = "PeripheralResponseReader"
    private var byteInputStream: ByteEndlessInputStream? = null
    private val responses = MutableSharedFlow<Flipper.Main>()
    private var responseReaderJob: Job? = null

    fun initialize() = runBlockingWithLog("initialize") {
        responseReaderJob?.cancelAndJoin()
        responseReaderJob = scope.launch {
            val byteInputStreamLocal = ByteEndlessInputStream(this)
            byteInputStream = byteInputStreamLocal
            parseLoopJob(byteInputStreamLocal)
        }
    }

    fun onReceiveBytes(byteArray: ByteArray) {
        info { "Receive proto array with size: ${byteArray.size}" }
        byteInputStream?.write(byteArray)
    }

    fun reset() = runBlockingWithLog("reset") {
        responseReaderJob?.cancelAndJoin()
        responseReaderJob = null
    }

    fun getResponses(): Flow<Flipper.Main> = responses

    private suspend fun CoroutineScope.parseLoopJob(byteInputStream: ByteEndlessInputStream) {
        while (this.isActive) {
            val main = Flipper.Main.parseDelimitedFrom(byteInputStream)
            info { "Receive $main response" }
            scope.launch(Dispatchers.Default) {
                responses.emit(main)
            }
        }
    }
}
