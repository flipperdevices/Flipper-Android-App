package com.flipperdevices.bridge.connection.feature.rpc.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.protobuf.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface FRpcFeatureApi : FDeviceFeatureApi {
    /**
     * You can subscribe on this flow if you want receive message without request
     */
    fun notificationFlow(): Flow<Main>

    /**
     * Send request and wait answer from them.
     *
     * You can use the extension Flow<Result<Main>>.toThrowableFlow(): Flow<Main> for more convenient error catching
     */
    fun request(command: FlipperRequest): Flow<Result<Main>>

    /**
     * Send request and wait single answer
     */
    suspend fun requestOnce(command: FlipperRequest): Result<Main>

    /**
     * Send batch of request in flipper and wait single answer
     */
    suspend fun request(
        commandFlow: Flow<FlipperRequest>,
        onCancel: suspend (Int) -> Unit = {}
    ): Result<Main>

    /**
     * Send batch request without waiting response
     */
    suspend fun requestWithoutAnswer(vararg commands: FlipperRequest)
}

/**
 * The correct way to handle an error on flow is to use Flow#onError.
 * But we can't just make a Flow<Main> interface - because that's dangerous.
 * We might forget to process the result.
 * So in order for you to handle errors on Flow correctly you need to specifically call this method
 * and be prepared for errors.
 *
 * Example of use:
 *
 * rpcFeatureApi.request(Main().wrapToRequest())
 *  .toThrowableFlow()
 *  .catch { throwable ->
 *   // Catch error
 *  }
 *  .collect {
 *   // Do action
 *  }
 */
fun Flow<Result<Main>>.toThrowableFlow(): Flow<Main> = map { it.getOrThrow() }