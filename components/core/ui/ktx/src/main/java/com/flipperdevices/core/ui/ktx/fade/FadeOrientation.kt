package com.flipperdevices.core.ui.ktx.fade

sealed interface FadeOrientation {
    data object Bottom : FadeOrientation {
        const val THRESHOLD: Float = DEFAULT_FADE_THRESHOLD
    }

    data object Top : FadeOrientation {
        const val THRESHOLD: Float = DEFAULT_FADE_THRESHOLD
    }

    companion object {
        private const val DEFAULT_FADE_THRESHOLD = 0.7f
    }
}
