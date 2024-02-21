package com.flipperdevices.keyemulate.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class EmulateProgress {
    @Immutable
    data object Infinite : EmulateProgress()

    @Immutable
    data class Growing(val duration: Long) : EmulateProgress()

    @Immutable
    data class GrowingAndStop(val duration: Long) : EmulateProgress()
}
