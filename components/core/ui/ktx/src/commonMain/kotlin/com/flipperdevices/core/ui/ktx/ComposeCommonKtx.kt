package com.flipperdevices.core.ui.ktx

import androidx.compose.runtime.Composable

@Composable
fun <T> T?.letCompose(block: @Composable (T) -> Unit): (@Composable () -> Unit)? {
    if (this == null) return null

    return { block(this) }
}
