package com.flipperdevices.inappnotification.impl.storage

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TimerTask(
    private val delayDuration: Duration,
    private val coroutineScope: CoroutineScope,
    private val block: suspend () -> Unit
) : LogTagProvider {
    override val TAG = "TimerTask"

    private val lock = ReentrantLock()
    private var job: Job? = null

    fun start() {
        lock.withLock {
            if (job == null) {
                job = coroutineScope.launch {
                    launchTimer()
                }
            }
        }
    }

    fun shutdown() {
        lock.withLock {
            runBlocking {
                job?.cancelAndJoin()
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun CoroutineScope.launchTimer() {
        while (isActive) {
            @Suppress("TooGenericExceptionCaught")
            try {
                block()
            } catch (blockExecutionError: Exception) {
                error(blockExecutionError) {
                    "While execute code block in timer we have error"
                }
            }
            delay(delayDuration)
        }
    }
}
