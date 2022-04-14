package com.flipperdevices.bridge.impl.manager

import com.flipperdevices.bridge.impl.utils.ByteEndlessInputStream
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
import kotlinx.coroutines.runBlocking

const val TAG = "PeripheralResponseReader"

@Suppress("BlockingMethodInNonBlockingContext")
class PeripheralResponseReader(
    private val scope: CoroutineScope
) : LogTagProvider {
    override val TAG = "PeripheralResponseReader"
    private var byteInputStream: ByteEndlessInputStream? = null
    private val responses = MutableSharedFlow<Flipper.Main>()
    private val responseReaderDispatcher = Dispatchers.Default.limitedParallelism(1)
    private var responseReaderJob: Job? = null

    suspend fun initialize() = runBlocking(responseReaderDispatcher) {
        byteInputStream?.stop()
        responseReaderJob?.cancelAndJoin()
        byteInputStream = ByteEndlessInputStream()
        responseReaderJob = parseLoopJob()
    }

    suspend fun onReceiveBytes(byteArray: ByteArray) = runBlocking(responseReaderDispatcher) {
        info { "Receive proto array with size: ${byteArray.size}" }
        byteInputStream?.write(byteArray)
    }

    suspend fun reset() = runBlocking(responseReaderDispatcher) {
        responseReaderJob?.cancelAndJoin()
        responseReaderJob = null
    }

    fun getResponses(): Flow<Flipper.Main> = responses

    private fun parseLoopJob(): Job = scope.launch(Dispatchers.Default) {
        val localByteInputStream = byteInputStream
        while (this.isActive) {
            val main = Flipper.Main.parseDelimitedFrom(localByteInputStream)
            info { "Receive $main response" }
            scope.launch {
                responses.emit(main)
            }
        }
    }
}
