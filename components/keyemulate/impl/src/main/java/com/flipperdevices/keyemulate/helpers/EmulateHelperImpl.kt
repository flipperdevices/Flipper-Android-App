package com.flipperdevices.keyemulate.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 *  It is very important for us not to call startEmulate if the application
 *  is already running - the flipper is very sensitive to the order of execution.
 */
@Singleton
@ContributesBinding(AppGraph::class, EmulateHelper::class)
class EmulateHelperImpl @Inject constructor(
    private val startEmulateHelper: StartEmulateHelper,
    private val stopEmulateHelper: StopEmulateHelper
) : EmulateHelper, LogTagProvider {
    override val TAG = "EmulateHelper"

    private var currentKeyEmulating = MutableStateFlow<EmulateConfig?>(null)

    @Volatile
    private var stopEmulateTimeAllowedMs: Long = 0
    private var stopJob: Job? = null
    private val mutex = Mutex()

    override fun getCurrentEmulatingKey(): StateFlow<EmulateConfig?> = currentKeyEmulating

    override suspend fun startEmulate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        config: EmulateConfig
    ) = withLockResult(mutex, "start") {
        val requestApi = serviceApi.requestApi
        if (currentKeyEmulating.value != null) {
            info { "Emulate already running, start stop" }
            stopEmulateInternal(requestApi)
        }
        currentKeyEmulating.emit(config)
        try {
            return@withLockResult startEmulateHelper.onStart(
                scope,
                serviceApi,
                config,
                onStop = { stopEmulateInternal(requestApi) },
                onResultTime = { time -> stopEmulateTimeAllowedMs = time }
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed start $config" }
            currentKeyEmulating.emit(null)
            throw throwable
        }
    }

    override suspend fun stopEmulate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi
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
            stopEmulateInternal(requestApi)
            return@withLock
        }
        stopJob = scope.launch(Dispatchers.Default) {
            try {
                while (TimeHelper.getNow() < stopEmulateTimeAllowedMs) {
                    val delayMs = max(0, stopEmulateTimeAllowedMs - TimeHelper.getNow())
                    info { "Can't stop right now, wait $delayMs ms" }
                    delay(delayMs)
                }
                launchWithLock(mutex, scope, "stop") {
                    stopEmulateInternal(requestApi)
                }
            } finally {
                stopJob = null
            }
        }
    }

    override suspend fun stopEmulateForce(
        requestApi: FlipperRequestApi
    ) = withLock(mutex, "force_stop") {
        if (stopJob != null) {
            stopJob?.cancelAndJoin()
            stopJob = null
        }
        stopEmulateInternal(requestApi)
    }

    private suspend fun stopEmulateInternal(requestApi: FlipperRequestApi) {
        stopEmulateHelper.onStop(requestApi)
        currentKeyEmulating.emit(null)
    }
}
