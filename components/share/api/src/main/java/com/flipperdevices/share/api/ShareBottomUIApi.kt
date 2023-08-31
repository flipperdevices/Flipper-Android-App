package com.flipperdevices.share.api

import androidx.compose.runtime.Composable

interface ShareBottomUIApi {
    @Composable
    fun ComposableShareBottomSheet(
        screenContent: @Composable (() -> Unit) -> Unit
    )
}
