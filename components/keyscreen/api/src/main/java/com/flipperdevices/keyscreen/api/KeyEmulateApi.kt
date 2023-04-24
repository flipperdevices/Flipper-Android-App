package com.flipperdevices.keyscreen.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKey

interface KeyEmulateApi {
    @Composable
    fun ComposableEmulateButton(modifier: Modifier, flipperKey: FlipperKey)

    @Composable
    fun ComposableEmulateInfraredButton(
        modifier: Modifier,
        flipperKey: FlipperKey,
        name: String
    )
}
