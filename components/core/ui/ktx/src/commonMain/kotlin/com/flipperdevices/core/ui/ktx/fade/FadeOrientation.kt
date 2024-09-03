package com.flipperdevices.core.ui.ktx.fade

import androidx.compose.runtime.Immutable

@Immutable
sealed interface FadeOrientation {
    val name: String

    @Immutable
    data object Bottom : FadeOrientation {
        override val name: String = "Bottom"

        const val THRESHOLD: Float = DEFAULT_FADE_THRESHOLD
    }

    @Immutable
    data object Top : FadeOrientation {
        override val name: String = "Top"

        const val THRESHOLD: Float = DEFAULT_FADE_THRESHOLD
    }

    companion object {
        private const val DEFAULT_FADE_THRESHOLD = 0.7f
    }
}
