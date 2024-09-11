package com.flipperdevices.bridge.connection.feature.storage.impl.fm.upload

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.WriteRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.ByteString

internal class WriteRequestLooper(
    private val rpcFeatureApi: FRpcFeatureApi,
    private val pathOnFlipper: String,
    private val priority: FlipperRequestPriority,
    scope: CoroutineScope
) {
    private val commands = MutableSharedFlow<FlipperRequest>()
    private val result = MutableStateFlow<Result<Main>?>(null)

    init {
        scope.launch {
            result.emit(
                rpcFeatureApi.request(
                    commandFlow = commands,
                    onCancel = {
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

    fun awaitResult() = runBlocking { result.filterNotNull().first() }

    fun writeSync(
        data: ByteString,
        hasNext: Boolean = true
    ): Unit = runBlocking {
        val waiter = MutableSharedFlow<Unit>()
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
                    waiter.emit(Unit)
                }
            )
        )
        waiter.first()
    }
}