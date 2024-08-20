package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

@Suppress("CompositionLocalAllowlist")
val LocalButtonPlaceholder = compositionLocalOf { ButtonPlaceholderState.NONE }

enum class ButtonPlaceholderState {
    NONE, SYNCING, EMULATING;

    val isEmulating: Boolean
        get() = this == EMULATING
    val isSyncing: Boolean
        get() = this == SYNCING
}

@Composable
internal fun ButtonPlaceholderComposition(
    isSyncing: Boolean,
    isEmulating: Boolean,
    content: @Composable () -> Unit
) {
    val buttonPlaceholderState = when {
        isSyncing -> ButtonPlaceholderState.SYNCING
        isEmulating -> ButtonPlaceholderState.EMULATING
        else -> ButtonPlaceholderState.NONE
    }
    CompositionLocalProvider(
        value = LocalButtonPlaceholder provides buttonPlaceholderState,
        content = content
    )
}
