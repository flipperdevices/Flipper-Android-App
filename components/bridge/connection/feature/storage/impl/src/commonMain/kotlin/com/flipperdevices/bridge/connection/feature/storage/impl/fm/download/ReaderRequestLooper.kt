package com.flipperdevices.bridge.connection.feature.storage.impl.fm.download

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.ReadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.Closeable

class ReaderRequestLooper(
    private val rpcFeatureApi: FRpcFeatureApi,
    private val pathOnFlipper: String,
    private val priority: FlipperRequestPriority,
    scope: CoroutineScope
) : Closeable {
    private val queue = Channel<Main>(Channel.UNLIMITED)
    private var isFinished = false

    private val responseFlowJob: Job = scope.launch(FlipperDispatchers.workStealingDispatcher) {
        rpcFeatureApi.request(
            Main(
                storage_read_request = ReadRequest(
                    path = pathOnFlipper
                )
            ).wrapToRequest(priority)
        ).collect { responseResult ->
            responseResult.onFailure {
                queue.close(it)
            }.onSuccess { response ->
                queue.send(response)
                if (response.has_next.not()) {
                    isFinished = true
                }
            }
        }
    }

    suspend fun getNextBytePack(): Main {
        return queue.receive()
    }

    override fun close() {
        runBlocking {
            responseFlowJob.cancel()
            queue.cancel()
        }
    }
}
