package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.core.log.verbose
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

internal class PendingResponseCounter(
    private val onAction: () -> Unit
) {
    private val counter = AtomicInteger(0)
    private val pendingCommands = ConcurrentHashMap<FlipperRequest, Unit>()

    fun hasPendingRequests(): Boolean {
        return if (counter.get() > 0) {
            true
        } else if (counter.get() < 0) {
            counter.set(0)
            if (BuildConfig.INTERNAL) {
                error("Pending response counter less than zero")
            }
            false
        } else {
            false
        }
    }

    suspend fun rememberAction(request: FlipperRequest?) {
        if (BuildConfig.INTERNAL && request != null) {
            pendingCommands[request] = Unit
        }
        val tag = request?.javaClass?.simpleName ?: ""
        onAction.invoke()
        val pendingCount = counter.getAndIncrement()
        verbose { "Increase pending response command $tag, current size is ${pendingCount + 1}" }
    }

    suspend fun forgetAction(request: FlipperRequest?) {
        if (BuildConfig.INTERNAL && request != null) {
            pendingCommands.remove(request)
        }
        val pendingCount = counter.decrementAndGet()
        verbose { "Decrease pending response command, current size is $pendingCount" }
    }

    companion object {
        internal const val LAGS_FLIPPER_DETECT_TIMEOUT_MS = 10 * 1000L // 10 seconds
    }
}
