package com.flipperdevices.bridge.api.manager.delegates

import com.flipperdevices.bridge.api.model.FlipperRequest
import kotlinx.coroutines.flow.Flow

/**
 * This service is designed to detect a flipper hangup and restart the connection.
 *
 * If we wait for a response from the flipper but get nothing,
 * we restart the connection to the flipper.
 */
interface FlipperLagsDetector {
    /**
     * @param request for debug purposes
     */
    suspend fun <T> wrapPendingAction(request: FlipperRequest?, block: suspend () -> T): T

    /**
     * @param request for debug purposes
     */
    fun <T> wrapPendingAction(request: FlipperRequest?, flow: Flow<T>): Flow<T>
}
