package com.flipperdevices.analytics.shake2report.listener

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
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
class FlipperShakeDetector(
    private val delegateListener: ShakeDetector.Listener,
    private val application: Application
) : ShakeDetector.Listener, BroadcastReceiver() {
    private val seismicShakeDetector = ShakeDetector(this)
    private var lastShakeTimestamp = 0L

    fun register() {
        val sensorManager = ContextCompat.getSystemService(application, SensorManager::class.java)
        seismicShakeDetector.start(sensorManager)

        application.registerReceiver(this, IntentFilter(SHAKE2REPORT_ACTION))
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
}
