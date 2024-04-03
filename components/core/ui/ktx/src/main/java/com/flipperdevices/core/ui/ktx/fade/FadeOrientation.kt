package com.flipperdevices.core.ui.ktx.fade

import androidx.annotation.FloatRange

sealed interface FadeOrientation {
    class Bottom(
        @FloatRange(0.0, 1.0)
        val threshold: Float = DEFAULT_FADE_THRESHOLD
    ) : FadeOrientation

    class Top(
        @FloatRange(0.0, 1.0)
        val threshold: Float = DEFAULT_FADE_THRESHOLD
    ) : FadeOrientation

    companion object {
        private const val DEFAULT_FADE_THRESHOLD = 0.7f
    }
}
