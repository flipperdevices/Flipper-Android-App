package com.flipperdevices.core.progress

import com.flipperdevices.core.buildkonfig.BuildKonfig
import java.lang.Float.min

private const val MAX_PERCENT = 1.0f
private const val MIN_PERCENT = 0f

class ProgressWrapperTracker(
    private val progressListener: ProgressListener,
    private val min: Float = MIN_PERCENT,
    private val max: Float = MAX_PERCENT
) : ProgressListener, FixedProgressListener {
    override suspend fun onProgress(current: Float) {
        val diff = max - min
        if (diff <= 0) { // This means that our min and max are originally incorrect
            return
        }

        val currentPercent = min + current * diff

        progressListener.onProgress(min(min(currentPercent, max), MAX_PERCENT))
    }

    override suspend fun onProgress(current: Long, max: Long) {
        if (current > max) {
            onProgress(MAX_PERCENT)
            if (BuildKonfig.IS_LOG_ENABLED) {
                error("Current larger then max (current: $current, max: $max)")
            }
            return
        }
        if (max == 0L) {
            onProgress(MAX_PERCENT)
            if (BuildKonfig.IS_LOG_ENABLED) {
                error("Max is zero")
            }
            return
        }

        val percent = current.toDouble() / max
        onProgress(percent.toFloat())
    }
}
