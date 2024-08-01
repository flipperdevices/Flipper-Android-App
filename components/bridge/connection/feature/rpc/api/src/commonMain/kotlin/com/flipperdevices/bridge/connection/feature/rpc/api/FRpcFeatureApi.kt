package com.flipperdevices.bridge.connection.feature.rpc.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.protobuf.Main
import kotlinx.coroutines.flow.Flow

interface FRpcFeatureApi : FDeviceFeatureApi {
    /**
     * You can subscribe on this flow if you want receive message without request
     */
    fun notificationFlow(): Flow<Main>

    /**
     * Send request and wait answer from them
     */
    fun request(command: FlipperRequest): Flow<Main>

    /**
     * Send batch of request in flipper and wait single answer
     */
    suspend fun request(
        commandFlow: Flow<FlipperRequest>,
        onCancel: suspend (Int) -> Unit = {}
    ): Main

    /**
     * Send batch request without waiting response
     */
    suspend fun requestWithoutAnswer(vararg commands: FlipperRequest)
}
