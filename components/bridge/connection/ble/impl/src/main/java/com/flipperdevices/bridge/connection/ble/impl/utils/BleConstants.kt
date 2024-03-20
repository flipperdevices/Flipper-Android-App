package com.flipperdevices.bridge.connection.ble.impl.utils

import java.util.concurrent.TimeUnit

object BleConstants {
    private const val CONNECT_TIME_SEC = 3L
    val CONNECT_TIME_MS = TimeUnit.MILLISECONDS.convert(CONNECT_TIME_SEC, TimeUnit.SECONDS)
    const val MAX_MTU = 512
}
