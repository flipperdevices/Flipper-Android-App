package com.flipperdevices.bridge.connection.transport.ble.impl.serial

import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

private const val DELAY_MS = 1000L

class SpeedMeter(scope: CoroutineScope) {
    private val bytesPerSecond = MutableStateFlow(0L)
    private var bytesCollected = AtomicLong(0)

    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            calculateSpeedEachSecond()
        }
    }

    fun getSpeed(): StateFlow<Long> = bytesPerSecond

    fun onReceiveBytes(bytesCount: Int) {
        bytesCollected.addAndGet(bytesCount.toLong())
    }

    private suspend fun calculateSpeedEachSecond() = withContext(FlipperDispatchers.workStealingDispatcher) {
        while (isActive) {
            delay(DELAY_MS)
            val totalBytes = bytesCollected.getAndSet(0)
            bytesPerSecond.update {
                totalBytes
            }
        }
    }
}
