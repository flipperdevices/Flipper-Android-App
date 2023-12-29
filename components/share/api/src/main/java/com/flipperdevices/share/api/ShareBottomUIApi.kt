package com.flipperdevices.share.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface ShareBottomUIApi {
    @Composable
    fun ComposableShareBottomSheet(
        flipperKeyPath: FlipperKeyPath,
        onSheetStateVisible: @Composable (isVisible: Boolean, onClose: () -> Unit) -> Unit,
        screenContent: @Composable (() -> Unit) -> Unit,
    )
}
