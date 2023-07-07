package com.flipperdevices.bridge.impl.manager

import com.flipperdevices.bridge.api.manager.service.RestartRPCApi
import com.flipperdevices.bridge.impl.utils.BridgeImplConfig.BLE_VLOG
import com.flipperdevices.bridge.impl.utils.ByteEndlessInputStream
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

const val TAG = "PeripheralResponseReader"

@Suppress("BlockingMethodInNonBlockingContext")
class PeripheralResponseReader(
    private val scope: CoroutineScope,
    private val sentryApi: Shake2ReportApi,
    private val restartRPCApi: RestartRPCApi
) : LogTagProvider {
    override val TAG = "PeripheralResponseReader"
    private val mutex = Mutex()
    private var byteInputStream: ByteEndlessInputStream? = null
    private val responses = MutableSharedFlow<Flipper.Main>()
    private var responseReaderJob: Job? = null

    suspend fun initialize() = withLock(mutex, "initialize") {
        responseReaderJob?.cancelAndJoin()
        responseReaderJob = scope.launch(Dispatchers.Default) {
            val byteInputStreamLocal = ByteEndlessInputStream(this)
            byteInputStream = byteInputStreamLocal
            parseLoopJob(byteInputStreamLocal)
        }
    }

    fun onReceiveBytes(byteArray: ByteArray) {
        if (BLE_VLOG) {
            info { "Receive proto array with size: ${byteArray.size}" }
        }
        byteInputStream?.write(byteArray)
    }

    suspend fun reset() = withLock(mutex, "reset") {
        responseReaderJob?.cancelAndJoin()
        responseReaderJob = null
    }

    fun getResponses(): Flow<Flipper.Main> = responses

    private suspend fun CoroutineScope.parseLoopJob(byteInputStream: ByteEndlessInputStream) {
        while (this.isActive) {
            try {
                val main = Flipper.Main.parseDelimitedFrom(byteInputStream)
                if (BLE_VLOG) {
                    info { "Receive $main response" }
                }
                scope.launch(Dispatchers.Default) {
                    responses.emit(main)
                }
            } catch (ignored: CancellationException) {
                // ignore
            } catch (ignored: java.util.concurrent.CancellationException) {
                // ignore
            } catch (invalidProtocol: InvalidProtocolBufferException) {
                error(invalidProtocol) { "Broke protocol" }
                restartRPCApi.restartRpc()
            } catch (e: Exception) {
                error(e) { "Failed parse stream" }
                sentryApi.reportException(e, "protobuf_read")
                restartRPCApi.restartRpc()
            }
        }
    }
}
