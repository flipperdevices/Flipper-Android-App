package com.flipperdevices.faphub.installation.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit

interface FapInstallationUIApi {
    @Composable
    fun ComposableButton(
        fapItemId: String?,
        modifier: Modifier,
        textSize: TextUnit
    )
}
