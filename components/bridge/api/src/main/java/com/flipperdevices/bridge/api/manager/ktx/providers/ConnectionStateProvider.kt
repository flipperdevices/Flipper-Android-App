package com.flipperdevices.bridge.api.manager.ktx.providers

import no.nordicsemi.android.ble.annotation.ConnectionState
import no.nordicsemi.android.ble.observer.ConnectionObserver

interface ConnectionStateProvider {
    fun isReady(): Boolean

    @ConnectionState
    fun getConnectionState(): Int
    fun subscribeOnConnectionState(observer: ConnectionObserver)
    fun unsubscribeConnectionState(observer: ConnectionObserver)
}
