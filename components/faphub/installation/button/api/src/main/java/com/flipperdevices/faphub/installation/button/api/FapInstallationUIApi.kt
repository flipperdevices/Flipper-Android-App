package com.flipperdevices.faphub.installation.button.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext

@Immutable
interface FapInstallationUIApi {
    @Composable
    @Suppress("NonSkippableComposable")
    fun ComposableButton(
        modifier: Modifier,
        componentContext: ComponentContext,
        config: FapButtonConfig?,
        fapButtonSize: FapButtonSize
    )
}
