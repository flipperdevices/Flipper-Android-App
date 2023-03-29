package com.flipperdevices.faphub.installation.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.installation.api.FapInstallationState
import com.flipperdevices.faphub.installation.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.impl.composable.ComposableFapInstallButton
import com.flipperdevices.faphub.installation.impl.composable.ComposableFapInstalledButton
import com.flipperdevices.faphub.installation.impl.composable.ComposableFapInstallingButton
import com.flipperdevices.faphub.installation.impl.composable.ComposableFapUpdateButton
import com.flipperdevices.faphub.installation.impl.composable.ComposableFapUpdatingButton
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstallationUIApi::class)
class FapInstallationUIApiImpl @Inject constructor() : FapInstallationUIApi {
    @Composable
    override fun ComposableButton(
        fapItem: FapItem?,
        modifier: Modifier,
        textSize: TextUnit,
        state: FapInstallationState
    ) {
        val buttonModifier = if (fapItem == null) {
            modifier.placeholderConnecting()
        } else {
            modifier
        }

        when (state) {
            FapInstallationState.Install -> ComposableFapInstallButton(
                modifier = buttonModifier,
                textSize = textSize
            )

            FapInstallationState.Installed -> ComposableFapInstalledButton(
                modifier = buttonModifier,
                textSize = textSize
            )

            FapInstallationState.Update -> ComposableFapUpdateButton(
                modifier = buttonModifier,
                textSize = textSize
            )

            is FapInstallationState.Installing -> ComposableFapInstallingButton(
                modifier = buttonModifier,
                textSize = textSize,
                percent = state.process
            )

            is FapInstallationState.Updating -> ComposableFapUpdatingButton(
                modifier = buttonModifier,
                textSize = textSize,
                percent = state.process
            )
        }
    }
}
