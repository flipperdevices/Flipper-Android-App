package com.flipperdevices.share.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface ShareBottomUIApi {
    @Composable
    fun ComposableShareBottomSheet(
        screenContent: @Composable (onShare: (FlipperKeyPath) -> Unit) -> Unit
    )
}
