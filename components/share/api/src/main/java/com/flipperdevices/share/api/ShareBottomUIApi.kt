package com.flipperdevices.share.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface ShareBottomUIApi {
    @Composable
    fun ComposableShareBottomSheet(
        provideFlipperKeyPath: () -> FlipperKeyPath,
        componentContext: ComponentContext,
        onSheetStateVisible: @Composable (isVisible: Boolean, onClose: () -> Unit) -> Unit,
        screenContent: @Composable (() -> Unit) -> Unit,
    )
}
