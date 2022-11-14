package com.flipperdevices.share.api

import androidx.compose.runtime.Composable

interface ShareBottomSheetApi {
    @Composable
    fun ComposableShareBottomSheet(onClose: () -> Unit)
}
