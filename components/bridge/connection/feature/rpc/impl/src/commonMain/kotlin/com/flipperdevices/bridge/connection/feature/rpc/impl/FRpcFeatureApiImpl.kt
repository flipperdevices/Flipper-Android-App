package com.flipperdevices.bridge.connection.feature.rpc.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.exception.resultOrError
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.rpc.storage.FRequestStorage
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.updateAndGetSafe
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.protobuf.Main
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

internal typealias OnReceiveResponse = suspend (Main) -> Unit

class FRpcFeatureApiImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val serialApi: FSerialDeviceApi,
    @Assisted private val lagsDetector: FLagsDetectorFeature,
    @Assisted private val restartApiFeature: FRestartRpcFeatureApi,
    messageProtobufParserFactory: FProtobufMessageCollector.Factory
) : FRpcFeatureApi, LogTagProvider {
    override val TAG = "FlipperRequestApi"

    // Start from 1 because 0 is default in protobuf
    private val idCounter = AtomicInteger(1)
    private val requestListeners = ConcurrentHashMap<Int, OnReceiveResponse>()
    private val requestStorage: FRequestStorage = FRequestStorage()
    private val messageProtobufParser = messageProtobufParserFactory(
        serialApi = serialApi,
        requestListeners = requestListeners,
        requestStorage = requestStorage,
        restartApiFeature = restartApiFeature,
        scope = scope
    )

    override fun notificationFlow() = messageProtobufParser.notificationFlow

    override fun request(
        command: FlipperRequest
    ): Flow<Result<Main>> = lagsDetector.wrapPendingAction(
        command,
        channelFlow {
            verbose { "Pending commands count: ${requestListeners.size}. Request $command" }
            // Generate unique ID for each command
            val uniqueId = findEmptyId(currentId = command.data.command_id)
            val requestWithId = command.copy(
                data = command.data.copy(
                    command_id = uniqueId
                )
            )

            // Add answer listener to listeners
            requestListeners[uniqueId] = {
                send(it.resultOrError())
                if (!it.has_next) {
                    requestListeners.remove(uniqueId)
                    close()
                }
            }

            requestStorage.sendRequest(requestWithId)

            awaitClose {
                requestStorage.removeRequest(requestWithId)
                requestListeners.remove(uniqueId)
            }
        }
    )

    override suspend fun requestOnce(
        command: FlipperRequest
    ): Result<Main> = lagsDetector.wrapPendingAction(command) {
        // Generate unique ID for each command
        val uniqueId = findEmptyId()

        val commandAnswerJob = scope.async {
            val result = awaitCommandAnswer(uniqueId)
            return@async result
        }

        val requestWithId = command.copy(
            data = command.data.copy(
                command_id = uniqueId
            )
        )

        requestStorage.sendRequest(requestWithId)

        val response = try {
            commandAnswerJob.await()
        } finally {
            withContext(NonCancellable) {
                commandAnswerJob.cancelAndJoin()
            }
        }
        return@wrapPendingAction response.resultOrError()
    }

    override suspend fun request(
        commandFlow: Flow<FlipperRequest>,
        onCancel: suspend (Int) -> Unit
    ): Result<Main> = lagsDetector.wrapPendingAction(null) {
        verbose { "Pending commands count: ${requestListeners.size}. Request command flow" }

        // Generate unique ID for each command
        val uniqueId = findEmptyId()
        // This is dirty way to understand if request is finished correctly with response
        var isFinished = false

        val commandAnswerJob = scope.async {
            val result = awaitCommandAnswer(uniqueId)
            isFinished = true
            return@async result
        }

        val flowCollectJob = commandFlow.onEach { request ->
            val requestWithId = request.copy(
                data = request.data.copy(
                    command_id = uniqueId
                )
            )
            requestStorage.sendRequest(requestWithId)
        }.onCompletion {
            if (it != null) {
                error(it) { "Cancel send because flow is failed" }
                commandAnswerJob.cancelAndJoin()
            }
        }.launchIn(scope + FlipperDispatchers.workStealingDispatcher)

        val response = try {
            commandAnswerJob.await()
        } finally {
            withContext(NonCancellable) {
                flowCollectJob.cancelAndJoin()
                commandAnswerJob.cancelAndJoin()
                if (!isFinished) {
                    info { "Requests with flow with id $uniqueId is canceled" }
                    onCancel(uniqueId)
                }
            }
        }

        return@wrapPendingAction response.resultOrError()
    }

    override suspend fun requestWithoutAnswer(vararg commands: FlipperRequest) {
        requestStorage.sendRequest(*commands)
    }

    private fun findEmptyId(currentId: Int = 0): Int {
        if (currentId != 0 && requestListeners[currentId] == null) {
            return currentId
        }

        var counter: Int
        do {
            counter = idCounter.updateAndGetSafe {
                if (it == Int.MAX_VALUE) {
                    return@updateAndGetSafe 1
                } else {
                    return@updateAndGetSafe it + 1
                }
            }
        } while (requestListeners[counter] != null)
        return counter
    }

    private suspend fun awaitCommandAnswer(
        uniqueId: Int
    ): Main = suspendCancellableCoroutine { cont ->
        requestListeners[uniqueId] = {
            requestListeners.remove(uniqueId)
            @OptIn(ExperimentalCoroutinesApi::class)
            cont.resume(it) { throwable ->
                error(throwable) { "Error on resume execution of $uniqueId command. Answer is $it" }
            }
        }

        cont.invokeOnCancellation {
            requestStorage.removeIf { request -> request.data.command_id == uniqueId }
            requestListeners.remove(uniqueId)
        }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            scope: CoroutineScope,
            serialApi: FSerialDeviceApi,
            lagsDetector: FLagsDetectorFeature,
            restartApiFeature: FRestartRpcFeatureApi
        ): FRpcFeatureApiImpl
    }
}
