package com.flipperdevices.bridge.connection.feature.rpc.reader

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.google.protobuf.InvalidProtocolBufferException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class PeripheralResponseReader @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val restartRPCApi: FRestartRpcFeatureApi,
    private val sentryApi: Shake2ReportApi
) : LogTagProvider {
    override val TAG = "PeripheralResponseReader"
    private val mutex = Mutex()
    private var byteInputStream: ByteEndlessInputStream? = null
    private val responses = MutableSharedFlow<Flipper.Main>()
    private var responseReaderJob: Job? = null

    init {
        scope.launch {
            initialize()
        }
    }

    private suspend fun initialize() = withLock(mutex, "initialize") {
        responseReaderJob?.cancelAndJoin()
        responseReaderJob = scope.launch(FlipperDispatchers.workStealingDispatcher) {
            val byteInputStreamLocal = ByteEndlessInputStream(this)
            byteInputStream = byteInputStreamLocal
            parseLoopJob(byteInputStreamLocal)
        }
    }

    fun onReceiveBytes(byteArray: ByteArray) {
        byteInputStream?.write(byteArray)
    }

    fun getResponses(): Flow<Flipper.Main> = responses

    private suspend fun CoroutineScope.parseLoopJob(byteInputStream: ByteEndlessInputStream) {
        while (this.isActive) {
            try {
                val main = Flipper.Main.parseDelimitedFrom(byteInputStream)
                scope.launch(FlipperDispatchers.workStealingDispatcher) {
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

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope,
            restartRPCApi: FRestartRpcFeatureApi,
        ): PeripheralResponseReader
    }
}
