package com.flipperdevices.share.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKey

interface ShareBottomSheetApi {
    @Composable
    fun ComposableShareBottomSheet(
        flipperKey: FlipperKey,
        modifier: Modifier,
        screenContent: @Composable () -> Unit
    )

    suspend fun showSheet()
}
