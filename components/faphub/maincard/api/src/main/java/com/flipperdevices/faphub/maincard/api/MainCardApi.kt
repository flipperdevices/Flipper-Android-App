package com.flipperdevices.faphub.maincard.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface MainCardApi {
    @Composable
    fun ComposableMainCard(
        modifier: Modifier,
        onClick: () -> Unit
    )
}