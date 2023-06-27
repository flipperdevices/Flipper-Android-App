package com.flipperdevices.faphub.installation.button.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapCancelingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstallButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstalledButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstallingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapNoInstallButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapUpdateButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapUpdatingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFlipperNotConnectedButton
import com.flipperdevices.faphub.installation.button.impl.viewmodel.FapStatusViewModel
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.squareup.anvil.annotations.ContributesBinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapInstallationUIApi::class)
class FapInstallationUIApiImpl @Inject constructor(
    private val bottomBarApi: BottomNavigationHandleDeeplink
) : FapInstallationUIApi {
    @Composable
    override fun ComposableButton(
        config: FapButtonConfig?,
        modifier: Modifier,
        fapButtonSize: FapButtonSize
    ) {
        val statusViewModel = tangleViewModel<FapStatusViewModel>()
        val stateFlow = remember(config) {
            statusViewModel.getStateForApplicationId(config)
        }
        val state by stateFlow.collectAsState(FapState.NotInitialized)

        when (val localState = state) {
            is FapState.InstallationInProgress -> ComposableFapInstallingButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                percent = localState.progress,
                onCancel = { statusViewModel.cancel(config) }
            )

            FapState.Installed -> ComposableFapInstalledButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize
            )

            FapState.ReadyToInstall -> ComposableFapInstallButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                onClick = { statusViewModel.install(config) }
            )

            FapState.NotInitialized,
            FapState.Deleting,
            FapState.RetrievingManifest -> ComposableFapInstalledButton(
                modifier = modifier.placeholderConnecting(),
                fapButtonSize = fapButtonSize
            )

            is FapState.ReadyToUpdate -> ComposableFapUpdateButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                onClick = { statusViewModel.update(config, localState.from) }
            )

            is FapState.UpdatingInProgress -> ComposableFapUpdatingButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                percent = localState.progress,
                onCancel = { statusViewModel.cancel(config) }
            )

            FapState.Canceling -> ComposableFapCancelingButton(
                fapButtonSize = fapButtonSize,
                modifier = modifier
            )

            FapState.ConnectFlipper -> ComposableFlipperNotConnectedButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                onOpenDeviceTab = {
                    bottomBarApi.onChangeTab(
                        tab = BottomBarTab.DEVICE,
                        force = true
                    )
                }
            )

            is FapState.NotAvailableForInstall -> ComposableFapNoInstallButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize
            )
        }
    }
}
