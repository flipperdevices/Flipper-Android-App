package com.flipperdevices.bridge.impl.utils

import kotlin.math.max
import kotlin.math.roundToLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val MS_IN_SEC = 1000L

class SpeedMeter {
    private val bytesPerSecond = MutableStateFlow(0L)
    private var lastTimestampMs = 0L

    fun getSpeed(): StateFlow<Long> = bytesPerSecond

    fun onReceiveBytes(bytesCount: Int) {
        if (lastTimestampMs == 0L) {
            bytesPerSecond.update { bytesCount.toLong() }
            lastTimestampMs = System.currentTimeMillis()
            return
        }

        val diffMs = max(System.currentTimeMillis() - lastTimestampMs, 1L)
        val receiveBytesInSecond: Long = (bytesCount.toDouble() * MS_IN_SEC / diffMs).roundToLong()

        bytesPerSecond.update {
            max(receiveBytesInSecond, 0L)
        }
        lastTimestampMs = System.currentTimeMillis()
    }
}
