package com.flipperdevices.bridge.service.impl.delegate

import com.flipperdevices.bridge.api.di.FlipperBleServiceGraph
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.flipperdevices.bridge.api.manager.delegates.FlipperLagsDetector
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Provider

@SingleIn(FlipperBleServiceGraph::class)
@ContributesBinding(FlipperBleServiceGraph::class, FlipperLagsDetector::class)
class FlipperLagsDetectorImpl @Inject constructor(
    scopeProvider: Provider<CoroutineScope>,
    serviceApiProvider: Provider<FlipperServiceApi>,
    flipperActionNotifierProvider: Provider<FlipperActionNotifier>
) : FlipperLagsDetector, LogTagProvider {
    override val TAG = "FlipperLagsDetector-${hashCode()}"

    private val scope by scopeProvider
    private val serviceApi by serviceApiProvider
    private val flipperActionNotifier by flipperActionNotifierProvider

    private val pendingCommands = ConcurrentHashMap<FlipperRequest, Unit>()
    private val pendingResponseCounter = AtomicInteger(0)

    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            combine(
                flipperActionNotifier.getActionFlow(),
                serviceApi.connectionInformationApi.getConnectionStateFlow()
            ) { _, connectionState ->
                connectionState
            }.collectLatest { connectionState ->
                delay(Constants.LAGS_FLIPPER_DETECT_TIMEOUT_MS)
                if (pendingResponseCounter.get() > 0) {
                    error {
                        "We have pending ${pendingResponseCounter.get()} commands, but flipper not respond " +
                            "${Constants.LAGS_FLIPPER_DETECT_TIMEOUT_MS}ms. Pending commands is " +
                            pendingCommands.keys().toList().joinToString()
                    }
                    if (connectionState is ConnectionState.Ready &&
                        connectionState.supportedState == FlipperSupportedState.READY
                    ) {
                        info { "Start restart RPC" }
                        serviceApi.restartRPC()
                    } else {
                        info { "Flipper not connected, so ignore it" }
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
        incrementPendingCounter(request?.javaClass?.simpleName ?: "")
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
            incrementPendingCounter(request?.javaClass?.simpleName ?: "")
        }.onEach {
            flipperActionNotifier.notifyAboutAction()
        }.onCompletion {
            if (BuildConfig.INTERNAL && request != null) {
                pendingCommands.remove(request)
            }
            val pendingCount = pendingResponseCounter.decrementAndGet()
            verbose { "Decrease pending response command, current size is $pendingCount" }
        }
    }

    private suspend fun incrementPendingCounter(tag: String) {
        flipperActionNotifier.notifyAboutAction()
        val pendingCount = pendingResponseCounter.getAndIncrement()
        verbose { "Increase pending response command $tag, current size is ${pendingCount + 1}" }
    }
}
