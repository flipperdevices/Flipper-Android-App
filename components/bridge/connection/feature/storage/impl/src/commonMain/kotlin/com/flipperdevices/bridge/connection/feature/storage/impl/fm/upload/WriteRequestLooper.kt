package com.flipperdevices.bridge.connection.feature.storage.impl.fm.upload

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.WriteRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.ByteString
import java.util.concurrent.Executors

internal class WriteRequestLooper(
    private val rpcFeatureApi: FRpcFeatureApi,
    private val pathOnFlipper: String,
    private val priority: FlipperRequestPriority,
    scope: CoroutineScope
) {
    private val commands = MutableSharedFlow<FlipperRequest>()
    private val result = MutableStateFlow<Result<Main>?>(null)
    private val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            result.emit(
                rpcFeatureApi.request(
                    commandFlow = commands,
                    onCancel = {
                        info { "#MAKEEVRSERGWriteRequestLooperInit onCancel" }
                        withContext(NonCancellable) {
                            rpcFeatureApi.requestOnce(
                                Main(
                                    has_next = false,
                                    storage_write_request = WriteRequest(path = pathOnFlipper)
                                ).wrapToRequest(FlipperRequestPriority.RIGHT_NOW)
                            )
                        }
                    }
                )
            )
        }
    }

    fun awaitResult() = runBlocking(dispatcher) { result.filterNotNull().first() }

    fun writeSync(
        data: ByteString,
        hasNext: Boolean = true
    ): Unit = runBlocking(dispatcher) {
        info { "#MAKEEVRSERGwriteSync" }
        val waiter = MutableStateFlow<Unit?>(null)
        info { "#MAKEEVRSERGwriteSync emit" }
        commands.emit(
            FlipperRequest(
                data = Main(
                    has_next = hasNext,
                    storage_write_request = WriteRequest(
                        path = pathOnFlipper,
                        file_ = File(data_ = data)
                    )
                ),
                priority = priority,
                onSendCallback = {
                    info { "#MAKEEVRSERGwriteSync onSedCallback" }
                    waiter.emit(Unit)
                }
            )
        )
        info { "#MAKEEVRSERGwriteSync waiter.receive()" }
        waiter.filterNotNull().first()
        info { "#MAKEEVRSERGwriteSync waiter.received!" }
    }
}
