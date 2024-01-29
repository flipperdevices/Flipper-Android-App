package com.flipperdevices.connection.impl.model

sealed class ConnectionStatusState {
    object NoDevice : ConnectionStatusState()
    object Disconnected : ConnectionStatusState()
    object Connecting : ConnectionStatusState()
    object Unsupported : ConnectionStatusState()
    object Connected : ConnectionStatusState()
    data class Synchronization(val progress: Float) : ConnectionStatusState()
    object Synchronized : ConnectionStatusState()
}
