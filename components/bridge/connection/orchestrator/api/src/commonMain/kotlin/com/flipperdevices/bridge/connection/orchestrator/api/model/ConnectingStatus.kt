package com.flipperdevices.bridge.connection.orchestrator.api.model

enum class ConnectingStatus {
    CONNECTING, // From init connection to first byte
    INITIALIZING // From first byte to "ready for use" state
}
