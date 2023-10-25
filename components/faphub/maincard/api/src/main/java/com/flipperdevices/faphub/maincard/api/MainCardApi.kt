package com.flipperdevices.faphub.maincard.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
interface MainCardApi {
    @Composable
    fun ComposableMainCard(
        modifier: Modifier,
        onClick: () -> Unit
    )
}
