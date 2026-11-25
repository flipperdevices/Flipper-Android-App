package com.flipperdevices.bridge.connection.livetests.api

/**
 * Single scenario executed against a real Flipper Zero device.
 *
 * Implementations must be safe to run sequentially inside one connection
 * session and must clean up everything they create on the device.
 */
interface LiveTest {
    val name: String
    val description: String

    /**
     * @return human-readable details on success or the failure reason
     */
    suspend fun execute(): Result<String>
}
