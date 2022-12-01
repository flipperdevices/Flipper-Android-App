package com.flipperdevices.faphub.installation.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import com.flipperdevices.faphub.dao.api.model.FapItem

interface FapInstallationUIApi {
    @Composable
    fun ComposableInstallButton(
        fapItem: FapItem?,
        modifier: Modifier,
        textSize: TextUnit
    )

    @Composable
    fun ComposableUpdateButton(
        fapItem: FapItem?,
        modifier: Modifier,
        textSize: TextUnit
    )
}
