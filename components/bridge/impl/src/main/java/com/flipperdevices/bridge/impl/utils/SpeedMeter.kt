package com.flipperdevices.bridge.impl.utils

import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MS_IN_SEC = 1000L

class SpeedMeter(scope: CoroutineScope) {
    private val bytesPerSecond = MutableStateFlow(0L)
    private var bytesCollected = AtomicLong(0)

    init {
        scope.launch {
            calculateSpeedEachSecond()
        }
    }

    fun getSpeed(): StateFlow<Long> = bytesPerSecond

    fun onReceiveBytes(bytesCount: Int) {
        bytesCollected.addAndGet(bytesCount.toLong())
    }

    private suspend fun calculateSpeedEachSecond() = withContext(Dispatchers.Default) {
        while (isActive) {
            delay(MS_IN_SEC)
            val totalBytes = bytesCollected.getAndSet(0)
            bytesPerSecond.update {
                totalBytes
            }
        }
    }
}
