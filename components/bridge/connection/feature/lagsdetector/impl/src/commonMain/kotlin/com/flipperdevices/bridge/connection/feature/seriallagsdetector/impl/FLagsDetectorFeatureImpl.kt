package com.flipperdevices.bridge.connection.feature.seriallagsdetector.impl

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequest
import com.flipperdevices.bridge.connection.feature.seriallagsdetector.api.FLagsDetectorFeature
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
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

class FLagsDetectorFeatureImpl @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted restartRpcFeatureApi: FRestartRpcFeatureApi,
    @Assisted private val flipperActionNotifier: FlipperActionNotifier
) : FLagsDetectorFeature, LogTagProvider {
    override val TAG = "FlipperLagsDetector-${hashCode()}"

    private val pendingResponseCounter = PendingResponseCounter(
        onAction = flipperActionNotifier::notifyAboutAction
    )

    init {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            flipperActionNotifier.getActionFlow().collectLatest { _ ->
                delay(PendingResponseCounter.LAGS_FLIPPER_DETECT_TIMEOUT_MS)
                if (pendingResponseCounter.hasPendingRequests()) {
                    error {
                        "We have pending commands, but flipper not respond " +
                            "${PendingResponseCounter.LAGS_FLIPPER_DETECT_TIMEOUT_MS}ms"
                    }
                    pendingResponseCounter.logPendingCommands()
                    info { "Start restart RPC" }

                    restartRpcFeatureApi.restartRpc()
                }
            }
        }
    }

    override fun notifyAboutAction() {
        flipperActionNotifier.notifyAboutAction()
    }

    override suspend fun <T> wrapPendingAction(
        request: FlipperRequest?,
        block: suspend () -> T
    ): T {
        pendingResponseCounter.rememberAction(request)
        val result = try {
            block()
        } finally {
            pendingResponseCounter.forgetAction(request)
        }
        return result
    }

    override fun <T> wrapPendingAction(request: FlipperRequest?, flow: Flow<T>): Flow<T> {
        return flow.onStart {
            pendingResponseCounter.rememberAction(request)
        }.onEach {
            flipperActionNotifier.notifyAboutAction()
        }.onCompletion {
            pendingResponseCounter.forgetAction(request)
        }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            scope: CoroutineScope,
            restartRpcFeatureApi: FRestartRpcFeatureApi,
            flipperActionNotifier: FlipperActionNotifier
        ): FLagsDetectorFeatureImpl
    }
}
