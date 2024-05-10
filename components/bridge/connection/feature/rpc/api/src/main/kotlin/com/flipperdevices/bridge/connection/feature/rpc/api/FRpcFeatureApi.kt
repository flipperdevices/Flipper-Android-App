package com.flipperdevices.bridge.connection.feature.rpc.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.protobuf.Flipper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface FRpcFeatureApi : FDeviceFeatureApi {
    /**
     * You can subscribe on this flow if you want receive message without request
     */
    fun notificationFlow(): Flow<Flipper.Main>

    /**
     * Send request and wait answer from them
     */
    fun request(command: FlipperRequest): Flow<Flipper.Main>

    /**
     * Send batch of request in flipper and wait single answer
     */
    suspend fun request(
        commandFlow: Flow<FlipperRequest>,
        onCancel: suspend (Int) -> Unit = {}
    ): Flipper.Main

    /**
     * Send batch request without waiting response
     */
    suspend fun requestWithoutAnswer(vararg commands: FlipperRequest)

    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope,
            serialApi: FSerialDeviceApi,
            lagsDetector: FLagsDetectorFeature
        ): FRpcFeatureApi
    }
}
