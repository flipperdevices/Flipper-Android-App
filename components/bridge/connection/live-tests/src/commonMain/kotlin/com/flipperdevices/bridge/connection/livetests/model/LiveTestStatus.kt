package com.flipperdevices.bridge.connection.livetests.model

sealed interface LiveTestStatus {
    data class Passed(val details: String) : LiveTestStatus
    data class Failed(val error: Throwable) : LiveTestStatus
}
