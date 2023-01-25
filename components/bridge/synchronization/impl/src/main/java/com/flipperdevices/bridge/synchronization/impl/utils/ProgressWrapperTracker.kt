package com.flipperdevices.bridge.synchronization.impl.utils

import com.flipperdevices.bridge.synchronization.impl.BuildConfig
import java.lang.Float.min

fun interface ProgressListener {
    suspend fun onProgress(current: Float)
}

class ProgressWrapperTracker(
    private val progressListener: ProgressListener,
    private val min: Float,
    private val max: Float
) : ProgressListener {
    override suspend fun onProgress(current: Float) {
        val diff = max - min
        if (diff <= 0) { // This means that our min and max are originally incorrect
            if (BuildConfig.DEBUG) {
                error("Incorrect min and max size (min: $min, max: $max)")
            }
            return
        }

        val currentPercent = min + current * diff
        if (BuildConfig.DEBUG && currentPercent > max) {
            error("Incorrect current percent (min: $min, current: $current, diff: $diff)")
        }

        progressListener.onProgress(min(min(currentPercent, max), 1.0f))
    }

    suspend fun report(current: Int, max: Int) {
        if (current > max) {
            onProgress(1.0f)
            if (BuildConfig.DEBUG) {
                error("Current larger then max (current: $current, max: $max)")
            }
            return
        }
        val percent = current.toFloat() / max.toFloat()
        onProgress(percent)
    }
}
