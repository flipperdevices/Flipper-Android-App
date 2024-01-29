package com.flipperdevices.faphub.maincard.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext

@Immutable
interface MainCardApi {
    @Composable
    @Suppress("NonSkippableComposable")
    fun ComposableMainCard(
        modifier: Modifier,
        componentContext: ComponentContext,
        onClick: () -> Unit
    )
}
