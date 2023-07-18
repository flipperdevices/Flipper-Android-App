package com.flipperdevices.faphub.installation.button.impl.composable.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstalledButton
import com.flipperdevices.faphub.installation.button.impl.composable.buttons.ComposableFapOpenButton
import com.flipperdevices.faphub.installation.button.impl.composable.buttons.ComposableFapOpeningButton
import com.flipperdevices.faphub.installation.button.impl.composable.dialogs.ComposableFlipperBusy
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.faphub.installation.button.impl.viewmodel.OpenFapViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableInstalledButton(
    config: FapButtonConfig?,
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier
) {
    val navController = LocalGlobalNavigationNavStack.current

    val viewModel = tangleViewModel<OpenFapViewModel>()

    val stateFlow = remember(config) { viewModel.getOpenFapState(config) }
    val state by stateFlow.collectAsState(OpenFapState.Loading)

    val dialogState by viewModel.getDialogState().collectAsState()
    ComposableFlipperBusy(showBusyDialog = dialogState) {
        viewModel.closeDialog()
    }

    when (val localState = state) {
        is OpenFapState.InProgress -> {
            if (localState.config == config) {
                ComposableFapOpeningButton(
                    modifier = modifier,
                    fapButtonSize = fapButtonSize,
                )
            } else {
                ComposableFapOpenButton(
                    modifier = modifier,
                    fapButtonSize = fapButtonSize,
                    onClick = { viewModel.open(config, navController) }
                )
            }
        }
        OpenFapState.NotSupported -> {
            ComposableFapInstalledButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize
            )
        }
        OpenFapState.Loading -> {
            ComposableFapInstalledButton(
                modifier = modifier.placeholderConnecting(),
                fapButtonSize = fapButtonSize
            )
        }
        OpenFapState.Ready -> {
            ComposableFapOpenButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                onClick = { viewModel.open(config, navController) }
            )
        }
    }
}
