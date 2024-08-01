package com.flipperdevices.bridge.connection.transport.ble.impl.utils

import java.util.concurrent.TimeUnit

object BleConstants {
    private const val CONNECT_TIME_SEC = 30L
    private const val PAIR_TIME_SEC = 60L
    val CONNECT_TIME_MS = TimeUnit.MILLISECONDS.convert(CONNECT_TIME_SEC, TimeUnit.SECONDS)
    val PAIR_TIME_MS = TimeUnit.MILLISECONDS.convert(PAIR_TIME_SEC, TimeUnit.SECONDS)
    const val MAX_MTU = 512
}
