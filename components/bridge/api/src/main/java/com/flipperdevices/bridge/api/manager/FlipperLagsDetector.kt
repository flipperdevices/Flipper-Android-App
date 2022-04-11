package com.flipperdevices.bridge.api.manager

import kotlinx.coroutines.flow.Flow

/**
 * This service is designed to detect a flipper hangup and restart the connection.
 *
 * If we wait for a response from the flipper but get nothing,
 * we restart the connection to the flipper.
 */
interface FlipperLagsDetector {
    suspend fun <T> wrapPendingAction(block: suspend () -> T): T
    fun <T> wrapPendingAction(flow: Flow<T>): Flow<T>
    fun notifyAboutAction()
    fun reset()
}
