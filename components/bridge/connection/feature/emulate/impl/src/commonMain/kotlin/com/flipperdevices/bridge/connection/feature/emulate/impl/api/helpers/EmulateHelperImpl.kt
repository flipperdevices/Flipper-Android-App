package com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.EmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StartEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StopEmulateHelper
import com.flipperdevices.bridge.connection.feature.emulate.api.model.EmulateConfig
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.math.max

/**
 *  It is very important for us not to call startEmulate if the application
 *  is already running - the flipper is very sensitive to the order of execution.
 */
class EmulateHelperImpl(
    private val startEmulateHelper: StartEmulateHelper,
    private val stopEmulateHelper: StopEmulateHelper
) : EmulateHelper, LogTagProvider {
    override val TAG = "EmulateHelper"

    private val currentKeyEmulating = MutableStateFlow<EmulateConfig?>(null)

    @Volatile
    private var stopEmulateTimeAllowedMs: Long = 0
    private var stopJob: Job? = null
    private val mutex = Mutex()

    override fun getCurrentEmulatingKey(): StateFlow<EmulateConfig?> = currentKeyEmulating

    override suspend fun startEmulate(
        scope: CoroutineScope,
        config: EmulateConfig
    ) = withLockResult(mutex, "start") {
        if (currentKeyEmulating.value != null) {
            info { "Emulate already running, start stop" }
            stopEmulateInternal()
        }
        currentKeyEmulating.emit(config)
        val isEmulateStarted = try {
            startEmulateHelper.onStart(
                scope,
                config,
                onStop = { stopEmulateHelper.onStop() },
                onResultTime = { time -> stopEmulateTimeAllowedMs = time }
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed start $config" }
            currentKeyEmulating.emit(null)
            throw throwable
        }
        if (!isEmulateStarted) {
            info { "Failed start $config but without crash" }
            currentKeyEmulating.emit(null)
        }
        return@withLockResult isEmulateStarted
    }

    override suspend fun stopEmulate(
        scope: CoroutineScope,
        isPressRelease: Boolean
    ) = withLock(mutex, "schedule_stop") {
        if (stopJob != null) {
            info { "Return from #stopEmulate because stop already in progress" }
            return@withLock
        }
        if (TimeHelper.getNow() > stopEmulateTimeAllowedMs) {
            info {
                "Already passed delay, stop immediately " +
                    "(current: ${TimeHelper.getNow()}/$stopEmulateTimeAllowedMs)"
            }
            stopEmulateInternal()
            return@withLock
        }
        stopJob = scope.launch(FlipperDispatchers.workStealingDispatcher) {
            try {
                while (TimeHelper.getNow() < stopEmulateTimeAllowedMs) {
                    val delayMs = max(0, stopEmulateTimeAllowedMs - TimeHelper.getNow())
                    info { "Can't stop right now, wait $delayMs ms" }
                    delay(delayMs)
                }
                launchWithLock(mutex, scope, "stop") {
                    stopEmulateInternal()
                }
            } finally {
                stopJob = null
            }
        }
    }

    override suspend fun stopEmulateForce(
        isPressRelease: Boolean
    ) = withLock(mutex, "force_stop") {
        if (stopJob != null) {
            stopJob?.cancelAndJoin()
            stopJob = null
        }
        stopEmulateInternal(isPressRelease)
    }

    private suspend fun stopEmulateInternal(
        isPressRelease: Boolean = false
    ) {
        stopEmulateHelper.onStop(isPressRelease)
        currentKeyEmulating.emit(null)
    }
}
