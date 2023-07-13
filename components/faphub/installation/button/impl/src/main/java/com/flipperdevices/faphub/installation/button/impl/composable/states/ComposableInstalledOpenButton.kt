package com.flipperdevices.faphub.installation.button.impl.composable.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstalledButton
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapOpenButton
import com.flipperdevices.faphub.installation.button.impl.composable.dialogs.ComposableFlipperBusy
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.faphub.installation.button.impl.viewmodel.OpenFapViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableInstalledOpenButton(
    config: FapButtonConfig?,
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier
) {
    val viewModel = tangleViewModel<OpenFapViewModel>()
    val state by remember(config) {
        viewModel.getOpenFapState(config)
    }.collectAsState()

    val dialogState by viewModel.getDialogState().collectAsState()
    ComposableFlipperBusy(showBusyDialog = dialogState) {
        viewModel.closeDialog()
    }

    when (state) {
        is OpenFapState.InProgress -> {}
        OpenFapState.NotSupported -> {
            ComposableFapInstalledButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize
            )
        }
        OpenFapState.Ready -> {
            val navController = LocalGlobalNavigationNavStack.current

            ComposableFapOpenButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                onClick = { viewModel.open(config, navController) }
            )
        }
    }
}
