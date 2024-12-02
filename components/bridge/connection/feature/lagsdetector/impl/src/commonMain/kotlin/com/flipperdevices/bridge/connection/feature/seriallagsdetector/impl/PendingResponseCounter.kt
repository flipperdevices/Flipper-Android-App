package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.core.log.error
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
            if (BuildKonfig.CRASH_APP_ON_FAILED_CHECKS) {
                error("Pending response counter less than zero")
            }
            false
        } else {
            false
        }
    }

    fun rememberAction(request: FlipperRequest?) {
        if (BuildKonfig.LOGGING_PENDING_COMMANDS && request != null) {
            pendingCommands[request] = Unit
        }
        val tag = request?.javaClass?.simpleName.orEmpty()
        onAction.invoke()
        val pendingCount = counter.getAndIncrement()
        verbose { "Increase pending response command $tag, current size is ${pendingCount + 1}" }
    }

    fun forgetAction(request: FlipperRequest?) {
        if (BuildKonfig.LOGGING_PENDING_COMMANDS && request != null) {
            pendingCommands.remove(request)
        }
        val pendingCount = counter.decrementAndGet()
        verbose { "Decrease pending response command, current size is $pendingCount" }
    }

    fun logPendingCommands() {
        if (BuildKonfig.LOGGING_PENDING_COMMANDS) {
            error { "Pending commands is: ${pendingCommands.keys.joinToString { it.data.toString() }}" }
        }
    }

    companion object {
        internal const val LAGS_FLIPPER_DETECT_TIMEOUT_MS = 30 * 1000L // 30 seconds
    }
}
