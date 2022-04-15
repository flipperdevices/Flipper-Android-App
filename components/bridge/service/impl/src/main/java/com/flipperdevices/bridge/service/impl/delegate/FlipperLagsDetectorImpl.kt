package com.flipperdevices.bridge.service.impl.delegate

import com.flipperdevices.bridge.api.manager.FlipperLagsDetector
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.impl.utils.WeakConnectionStateProvider
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FlipperLagsDetectorImpl(
    private val scope: CoroutineScope,
    private val serviceApi: FlipperServiceApi,
    private val connectionStateProvider: WeakConnectionStateProvider
) : FlipperLagsDetector, LogTagProvider {
    override val TAG = "FlipperLagsDetector"

    private val pendingCommands = ConcurrentHashMap<FlipperRequest, Unit>()
    private val pendingResponseCounter = AtomicInteger(0)
    private val pendingResponseFlow = MutableSharedFlow<Unit>()

    init {
        scope.launch(Dispatchers.Default) {
            combine(
                pendingResponseFlow,
                connectionStateProvider.getConnectionFlow()
            ) { _, connectionState ->
                connectionState
            }.collectLatest { connectionState ->
                delay(Constants.LAGS_FLIPPER_DETECT_TIMEOUT_MS)
                if (pendingResponseCounter.get() > 0) {
                    error {
                        "We have pending commands, but flipper not respond " +
                            "${Constants.LAGS_FLIPPER_DETECT_TIMEOUT_MS}ms. Pending commands is " +
                            pendingCommands.keys().toList().joinToString()
                    }
                    if (connectionState is ConnectionState.Ready && connectionState.isSupported) {
                        // serviceApi.reconnect()
                    }
                } else if (pendingResponseCounter.get() < 0) {
                    pendingResponseCounter.set(0)
                    if (BuildConfig.INTERNAL) {
                        error("Pending response counter less than zero")
                    }
                }
            }
        }
    }

    override suspend fun <T> wrapPendingAction(
        request: FlipperRequest?,
        block: suspend () -> T
    ): T {
        if (BuildConfig.INTERNAL && request != null) {
            pendingCommands[request] = Unit
        }
        incrementPendingCounter()
        val result = try {
            block()
        } finally {
            if (BuildConfig.INTERNAL && request != null) {
                pendingCommands.remove(request)
            }
            val pendingCount = pendingResponseCounter.decrementAndGet()
            verbose { "Decrease pending response command, current size is $pendingCount" }
        }
        return result
    }

    override fun <T> wrapPendingAction(request: FlipperRequest?, flow: Flow<T>): Flow<T> {
        return flow.onStart {
            if (BuildConfig.INTERNAL && request != null) {
                pendingCommands[request] = Unit
            }
            incrementPendingCounter()
        }.onEach {
            notifyAboutAction()
        }.onCompletion {
            if (BuildConfig.INTERNAL && request != null) {
                pendingCommands.remove(request)
            }
            val pendingCount = pendingResponseCounter.decrementAndGet()
            verbose { "Decrease pending response command, current size is $pendingCount" }
        }
    }

    private suspend fun incrementPendingCounter() {
        val pendingCount = pendingResponseCounter.getAndIncrement()
        verbose { "Increase pending response command, current size is ${pendingCount + 1}" }
        if (pendingCount == 0) {
            info { "Pending response count is zero, so we launch pending flow" }
            pendingResponseFlow.emit(Unit)
        }
    }

    override fun notifyAboutAction() {
        verbose { "Receive that flipper active" }
        scope.launch(Dispatchers.Default) {
            pendingResponseFlow.emit(Unit)
        }
    }
}
