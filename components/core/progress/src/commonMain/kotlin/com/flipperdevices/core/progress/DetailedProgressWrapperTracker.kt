package com.flipperdevices.core.progress

import com.flipperdevices.core.buildkonfig.BuildKonfig
import kotlin.math.min

private const val MAX_PERCENT = 1.0f
private const val MIN_PERCENT = 0f

class DetailedProgressWrapperTracker(
    private val progressListener: DetailedProgressListener,
    private val min: Float = MIN_PERCENT,
    private val max: Float = MAX_PERCENT
) : DetailedProgressListener {
    override suspend fun onProgress(current: Float, detail: DetailedProgressListener.Detail) {
        val diff = max - min
        if (diff <= 0) { // This means that our min and max are originally incorrect
            return
        }

        val currentPercent = min + current * diff

        progressListener.onProgress(
            min(
                min(currentPercent, max),
                MAX_PERCENT
            ),
            detail
        )
    }

    suspend fun report(current: Long, max: Long, detail: DetailedProgressListener.Detail) {
        if (current > max) {
            onProgress(MAX_PERCENT, detail)
            if (BuildKonfig.IS_LOG_ENABLED) {
                error("Current larger then max (current: $current, max: $max)")
            }
            return
        }
        if (max == 0L) {
            onProgress(MAX_PERCENT, detail)
            if (BuildKonfig.IS_LOG_ENABLED) {
                error("Max is zero")
            }
            return
        }

        val percent = current.toDouble() / max
        onProgress(percent.toFloat(), detail)
    }
}
