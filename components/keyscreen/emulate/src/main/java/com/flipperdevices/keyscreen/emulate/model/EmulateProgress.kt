package com.flipperdevices.keyscreen.emulate.model

import androidx.compose.runtime.Stable

@Stable
sealed class EmulateProgress {
    @Stable
    object Infinite : EmulateProgress()

    @Stable
    data class Fixed(val progress: Long, val total: Long) : EmulateProgress() {
        fun toProgressFloat() = if (total != 0L) {
            progress.toFloat() / total.toFloat()
        } else 0f
    }
}
