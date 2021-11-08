package com.flipperdevices.bridge.api.manager

import com.flipperdevices.protobuf.Flipper
import kotlinx.coroutines.flow.Flow

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
    fun request(command: Flipper.Main): Flow<Flipper.Main>

    /**
     * Send batch request without waiting response
     */
    suspend fun requestWithoutAnswer(vararg command: Flipper.Main)
}
