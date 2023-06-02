package com.flipperdevices.keyemulate.model

import androidx.compose.runtime.Stable

@Stable
sealed class EmulateProgress {
    @Stable
    object Infinite : EmulateProgress()

    @Stable
    data class Growing(val duration: Long) : EmulateProgress()

    @Stable
    data class GrowingAndStop(val duration: Long) : EmulateProgress()
}
