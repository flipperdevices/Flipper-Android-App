package com.flipperdevices.faphub.installation.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.installation.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.impl.composable.ComposableFapInstallationButton
import com.flipperdevices.faphub.installation.impl.composable.ComposableFapUpdateButton
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstallationUIApi::class)
class FapInstallationUIApiImpl @Inject constructor() : FapInstallationUIApi {
    @Composable
    override fun ComposableInstallButton(
        fapItem: FapItem?,
        modifier: Modifier,
        textSize: TextUnit
    ) {
        ComposableFapInstallationButton(
            textSize,
            if (fapItem == null) {
                modifier.placeholderConnecting()
            } else {
                modifier
            },
        )
    }

    @Composable
    override fun ComposableUpdateButton(
        fapItem: FapItem?,
        modifier: Modifier,
        textSize: TextUnit
    ) {
        ComposableFapUpdateButton(
            textSize,
            if (fapItem == null) {
                modifier.placeholderConnecting()
            } else {
                modifier
            }
        )
    }
}
