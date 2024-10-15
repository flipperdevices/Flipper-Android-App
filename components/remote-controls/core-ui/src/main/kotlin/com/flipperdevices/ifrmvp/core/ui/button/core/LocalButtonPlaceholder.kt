package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

@Suppress("CompositionLocalAllowlist")
val LocalButtonPlaceholder = compositionLocalOf { ButtonPlaceholderState.NONE }

enum class ButtonPlaceholderState {
    NONE, NOT_CONNECTED, SYNCING, EMULATING;

    val isEmulating: Boolean
        get() = this == EMULATING
    val isSyncing: Boolean
        get() = this == SYNCING
    val isConnected: Boolean
        get() = this != NOT_CONNECTED
}

@Composable
internal fun ButtonPlaceholderComposition(
    isSyncing: Boolean,
    isEmulating: Boolean,
    isConnected: Boolean,
    content: @Composable () -> Unit
) {
    val buttonPlaceholderState = when {
        !isConnected -> ButtonPlaceholderState.NOT_CONNECTED
        isSyncing -> ButtonPlaceholderState.SYNCING
        isEmulating -> ButtonPlaceholderState.EMULATING
        else -> ButtonPlaceholderState.NONE
    }
    CompositionLocalProvider(
        value = LocalButtonPlaceholder provides buttonPlaceholderState,
        content = {
            content.invoke()
        }
    )
}
