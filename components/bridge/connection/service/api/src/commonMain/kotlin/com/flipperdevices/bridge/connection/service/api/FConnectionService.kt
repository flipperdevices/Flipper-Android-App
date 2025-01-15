package com.flipperdevices.bridge.connection.service.api

/**
 * This service handle connection for current flipper device
 */
interface FConnectionService {
    /**
     * Should be called once on application start
     */
    fun onApplicationInit()

    /**
     * Reconnect to last known device after being force disconnected
     */
    fun forceReconnect()

    /**
     * Disconnect current device
     * @param force if true, will not reconnect until [forceReconnect]
     */
    fun disconnect(force: Boolean = false)

    /**
     * Forget current device and disconnect from it as side effect
     */
    fun forgetCurrentDevice()

    /**
     * Connect devices only if it wasn't force disconnected
     */
    fun connectIfNotForceDisconnect()
}
