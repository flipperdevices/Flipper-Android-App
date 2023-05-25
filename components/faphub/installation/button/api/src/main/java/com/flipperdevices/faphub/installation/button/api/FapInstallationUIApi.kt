package com.flipperdevices.faphub.installation.button.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit

interface FapInstallationUIApi {
    @Composable
    fun ComposableButton(
        config: FapButtonConfig?,
        modifier: Modifier,
        textSize: TextUnit
    )
}
