package com.flipperdevices.bridge.impl.manager

import com.flipperdevices.bridge.impl.utils.ByteEndlessInputStream
import com.flipperdevices.core.ktx.newSingleThreadExecutor
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG = "PeripheralResponseReader"

@Suppress("BlockingMethodInNonBlockingContext")
class PeripheralResponseReader(
    private val scope: CoroutineScope
) : LogTagProvider {
    override val TAG = "PeripheralResponseReader"
    private val byteInputStream = ByteEndlessInputStream()
    private val responses = MutableSharedFlow<Flipper.Main>()
    private val responseReaderDispatcher = newSingleThreadExecutor(TAG)
        .asCoroutineDispatcher()

    init {
        scope.launch {
            withContext(responseReaderDispatcher) {
                while (this.isActive) {
                    val main = Flipper.Main.parseDelimitedFrom(byteInputStream)
                    info { "Receive $main response" }
                    scope.launch {
                        responses.emit(main)
                    }
                }
            }
        }
    }

    fun onReceiveBytes(byteArray: ByteArray) {
        info { "Receive proto array with size: ${byteArray.size}" }
        byteInputStream.write(byteArray)
    }

    fun getResponses(): Flow<Flipper.Main> = responses
}
