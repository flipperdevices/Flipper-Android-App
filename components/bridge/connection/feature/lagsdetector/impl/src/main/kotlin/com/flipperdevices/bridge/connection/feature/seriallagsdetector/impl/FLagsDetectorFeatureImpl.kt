package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FlipperRequest
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private const val LAGS_FLIPPER_DETECT_TIMEOUT_MS = 10 * 1000L // 10 seconds

class FLagsDetectorFeatureImpl @AssistedInject constructor(
    @Assisted scope: CoroutineScope,
    @Assisted restartRpcFeatureApi: FRestartRpcFeatureApi,
) : FLagsDetectorFeature, LogTagProvider {
    override val TAG = "FlipperLagsDetector-${hashCode()}"

    private val flipperActionNotifier = FlipperActionNotifierImpl(scope = scope)

    private val pendingCommands = ConcurrentHashMap<FlipperRequest, Unit>()
    private val pendingResponseCounter = AtomicInteger(0)

    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            flipperActionNotifier.getActionFlow().collectLatest { connectionState ->
                delay(LAGS_FLIPPER_DETECT_TIMEOUT_MS)
                if (pendingResponseCounter.get() > 0) {
                    error {
                        "We have pending ${pendingResponseCounter.get()} commands, but flipper not respond " +
                            "${LAGS_FLIPPER_DETECT_TIMEOUT_MS}ms. Pending commands is " +
                            pendingCommands.keys().toList().joinToString()
                    }
                    info { "Start restart RPC" }

                    restartRpcFeatureApi.restartRpc()
                } else if (pendingResponseCounter.get() < 0) {
                    pendingResponseCounter.set(0)
                    if (BuildConfig.INTERNAL) {
                        error("Pending response counter less than zero")
                    }
                }
            }
        }
    }

    override fun notifyAboutAction() = flipperActionNotifier.notifyAboutAction()

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

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            scope: CoroutineScope,
            restartRpcFeatureApi: FRestartRpcFeatureApi,
        ): FLagsDetectorFeatureImpl
    }
}
