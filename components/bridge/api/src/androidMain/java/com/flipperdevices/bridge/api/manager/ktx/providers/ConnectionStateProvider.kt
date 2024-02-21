package com.flipperdevices.bridge.api.manager.ktx.providers

import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.manager.observers.SuspendConnectionObserver
import no.nordicsemi.android.ble.annotation.ConnectionState

interface ConnectionStateProvider {
    fun isReady(): Boolean

    /**
     * @return null if unknown
     * false if device not supported
     * true if device supported
     */
    fun supportState(): FlipperSupportedState?

    @ConnectionState
    fun getConnectionState(): Int
    fun subscribeOnConnectionState(observer: SuspendConnectionObserver)
    fun unsubscribeConnectionState(observer: SuspendConnectionObserver)
}
