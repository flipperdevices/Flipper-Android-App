package com.flipperdevices.bridge.service.impl.delegate

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.flipperdevices.bridge.api.manager.delegates.FlipperActionNotifier
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.ktx.jre.combine
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FlipperAutoDisconnect(
    private val scope: CoroutineScope,
    private val flipperActionNotifier: FlipperActionNotifier,
    private val serviceApi: FlipperServiceApi,
    private val application: Application
) : LogTagProvider, Application.ActivityLifecycleCallbacks {
    override val TAG = "FlipperAutoDisconnect"

    private val activityVisible = AtomicBoolean(true)
    private val openActivityFlow = MutableSharedFlow<Unit>()
    private var disconnectDelayJob: Job? = null

    fun init() {
        info { "Init start" }
        application.registerActivityLifecycleCallbacks(this)
        disconnectDelayJob?.cancel()
        disconnectDelayJob = scope.launch(Dispatchers.Default) {
            flipperActionNotifier.getActionFlow().combine(openActivityFlow).collectLatest {
                delay(Constants.FLIPPER_AUTODISCONNECT_TIMEOUT_MS)
                if (activityVisible.get()) {
                    return@collectLatest
                }
                info { "Try auto disconnect flipper" }
                serviceApi.disconnect()
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        activityVisible.set(true)
    }

    override fun onActivityPaused(activity: Activity) {
        activityVisible.set(false)
        scope.launch { openActivityFlow.emit(Unit) }
    }

    // Unused fun
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
