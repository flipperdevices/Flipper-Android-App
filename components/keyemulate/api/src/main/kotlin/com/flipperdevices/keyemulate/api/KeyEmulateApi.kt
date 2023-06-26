package com.flipperdevices.keyemulate.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.keyemulate.model.EmulateConfig

interface KeyEmulateApi {
    @Composable
    fun ComposableEmulateButton(
        modifier: Modifier,
        emulateConfig: EmulateConfig,
        isSynchronized: Boolean
    )
}
