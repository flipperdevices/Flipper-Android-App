package com.flipperdevices.faphub.installation.button.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapCancelingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstallButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstalledButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstallingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapUpdateButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapUpdatingButton
import com.flipperdevices.faphub.installation.button.impl.viewmodel.FapStatusViewModel
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import tangle.viewmodel.compose.tangleViewModel

@ContributesBinding(AppGraph::class, FapInstallationUIApi::class)
class FapInstallationUIApiImpl @Inject constructor() : FapInstallationUIApi {
    @Composable
    override fun ComposableButton(
        config: FapButtonConfig?,
        modifier: Modifier,
        textSize: TextUnit
    ) {
        val statusViewModel = tangleViewModel<FapStatusViewModel>()
        val state by statusViewModel.getStateForApplicationId(config).collectAsState()

        when (val localState = state) {
            is FapState.InstallationInProgress -> ComposableFapInstallingButton(
                modifier = modifier,
                textSize = textSize,
                percent = localState.progress
            )

            FapState.Installed -> ComposableFapInstalledButton(
                modifier = modifier,
                textSize = textSize
            )

            FapState.ReadyToInstall -> ComposableFapInstallButton(
                modifier = modifier,
                textSize = textSize,
                onClick = { statusViewModel.install(config) }
            )

            FapState.NotInitialized,
            FapState.RetrievingManifest -> ComposableFapInstalledButton(
                modifier = modifier.placeholderConnecting(),
                textSize = textSize
            )

            is FapState.ReadyToUpdate -> ComposableFapUpdateButton(
                modifier = modifier,
                textSize = textSize,
                onClick = { statusViewModel.update(config, localState.from) }
            )

            is FapState.UpdatingInProgress -> ComposableFapUpdatingButton(
                modifier = modifier,
                textSize = textSize,
                percent = localState.progress
            )

            FapState.Canceling -> ComposableFapCancelingButton(
                textSize = textSize,
                modifier = modifier
            )
        }
    }
}
