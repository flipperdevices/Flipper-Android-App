package com.flipperdevices.connection.impl.model

sealed class ConnectionStatusState {
    object Disconnected : ConnectionStatusState()
    object Connecting : ConnectionStatusState()
    object Synchronization : ConnectionStatusState()
    object Completed : ConnectionStatusState()
}
