package com.flipperdevices.analytics.shake2report.impl.listener

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.squareup.seismic.ShakeDetector
import java.util.concurrent.TimeUnit

private val SHAKE2REPORT_TIMEOUT_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)

// For call adb shell am broadcast -a com.flipperdevices.SHAKE_ACTION from console
private const val SHAKE2REPORT_ACTION = "com.flipperdevices.SHAKE_ACTION"

/**
 * + Removes repetitive listener calls
 * + Add ability shake via adb/console
 */
@Suppress("TooManyFunctions")
class FlipperShakeDetector(
    private val delegateListener: ShakeDetector.Listener,
    private val application: Application
) : ShakeDetector.Listener, BroadcastReceiver(), Application.ActivityLifecycleCallbacks {
    private val seismicShakeDetector = ShakeDetector(this)
    private var lastShakeTimestamp = 0L

    fun register() {
        val sensorManager = ContextCompat.getSystemService(application, SensorManager::class.java)
        seismicShakeDetector.start(sensorManager)

        application.registerReceiver(this, IntentFilter(SHAKE2REPORT_ACTION))
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * Calls from seismic shake detector
     */
    override fun hearShake() {
        onShakeInternal()
    }

    private fun onShakeInternal() {
        // Sometimes hearShake calls twice or more
        if (System.currentTimeMillis() - lastShakeTimestamp < SHAKE2REPORT_TIMEOUT_MS) {
            return
        }
        lastShakeTimestamp = System.currentTimeMillis()

        delegateListener.hearShake()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SHAKE2REPORT_ACTION) {
            onShakeInternal()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        val sensorManager = ContextCompat.getSystemService(application, SensorManager::class.java)
        seismicShakeDetector.start(sensorManager)
    }

    override fun onActivityPaused(activity: Activity) {
        seismicShakeDetector.stop()
    }

    // Unused fun
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
