package com.flipperdevices.newfilemanager.impl.model

import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed

sealed interface SpeedState {
    data class Ready(
        val receiveBytesInSec: Long = 0L,
        val transmitBytesInSec: Long = 0L
    ) : SpeedState {
        constructor(serialSpeed: FlipperSerialSpeed) : this(
            serialSpeed.receiveBytesInSec,
            serialSpeed.transmitBytesInSec
        )
    }

    data object Unknown : SpeedState
}
