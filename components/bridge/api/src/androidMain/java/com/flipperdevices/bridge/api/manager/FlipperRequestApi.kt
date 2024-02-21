package com.flipperdevices.bridge.api.manager

import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.protobuf.Flipper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * High level API for Flipper RPC
 */
interface FlipperRequestApi {
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

    /**
     * Contains state about average serial speed
     */
    suspend fun getSpeed(): StateFlow<FlipperSerialSpeed>

    /**
     * Method for connection break debug - put random bytes into session
     */
    suspend fun sendTrashBytesAndBrokeSession()
}
