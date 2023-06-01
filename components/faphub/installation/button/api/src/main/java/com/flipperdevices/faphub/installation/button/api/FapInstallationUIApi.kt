package com.flipperdevices.faphub.installation.button.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface FapInstallationUIApi {
    @Composable
    fun ComposableButton(
        config: FapButtonConfig?,
        modifier: Modifier,
        fapButtonSize: FapButtonSize
    )
}
