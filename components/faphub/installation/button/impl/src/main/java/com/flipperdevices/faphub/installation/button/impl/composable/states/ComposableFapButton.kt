package com.flipperdevices.faphub.installation.button.impl.composable.states

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapCancelingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstallButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstalledButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstallingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapNoInstallButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapUpdateButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapUpdatingButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFlipperNoSdCardButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFlipperNotConnectedButton
import com.flipperdevices.faphub.installation.button.impl.viewmodel.FapStatusViewModel
import com.flipperdevices.faphub.installation.button.impl.viewmodel.OpenFapViewModel
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installation.stateprovider.api.model.NotAvailableReason

@Composable
@Suppress("LongMethod")
internal fun ComposableFapButton(
    localState: FapState,
    fapButtonSize: FapButtonSize,
    statusViewModel: FapStatusViewModel,
    config: FapButtonConfig?,
    openViewModel: OpenFapViewModel,
    modifier: Modifier = Modifier
) {
    when (localState) {
        is FapState.InstallationInProgress -> ComposableFapInstallingButton(
            modifier = modifier,
            fapButtonSize = fapButtonSize,
            percent = localState.progress,
            onCancel = { statusViewModel.cancel(config) }
        )

        FapState.Installed ->
            ComposableInstalledButton(
                config = config,
                fapButtonSize = fapButtonSize,
                modifier = modifier,
                viewModel = openViewModel
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

        is FapState.NotAvailableForInstall -> when (localState.reason) {
            NotAvailableReason.BUILD_RUNNING,
            NotAvailableReason.UNSUPPORTED_APP,
            NotAvailableReason.FLIPPER_OUTDATED,
            NotAvailableReason.UNSUPPORTED_SDK -> ComposableFapNoInstallButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize
            )
            NotAvailableReason.NO_SD_CARD -> ComposableFlipperNoSdCardButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
            )
            NotAvailableReason.FLIPPER_NOT_CONNECTED -> ComposableFlipperNotConnectedButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize
            )
        }
    }
}
