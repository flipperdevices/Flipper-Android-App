package com.flipperdevices.faphub.installation.button.impl.composable.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.navigation.LocalGlobalNavigationNavStack
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.installation.button.api.FapButtonConfig
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.R
import com.flipperdevices.faphub.installation.button.impl.composable.ComposableFapInstalledButton
import com.flipperdevices.faphub.installation.button.impl.composable.dialogs.ComposableFlipperBusy
import com.flipperdevices.faphub.installation.button.impl.composable.elements.ComposableFlipperButton
import com.flipperdevices.faphub.installation.button.impl.model.OpenFapState
import com.flipperdevices.faphub.installation.button.impl.viewmodel.OpenFapViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableInstalledOpenButton(
    config: FapButtonConfig?,
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier
) {
    val navController = LocalGlobalNavigationNavStack.current

    val viewModel = tangleViewModel<OpenFapViewModel>()
    val state by viewModel.getOpenFapState().collectAsState()

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
        OpenFapState.Ready -> {
            ComposableFapOpenButton(
                modifier = modifier,
                fapButtonSize = fapButtonSize,
                onClick = { viewModel.open(config, navController) }
            )
        }
    }
}

@Composable
private fun ComposableFapOpenButton(
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val text = when (fapButtonSize) {
        FapButtonSize.COMPACTED -> stringResource(R.string.faphub_installation_open)
        FapButtonSize.LARGE -> stringResource(R.string.faphub_installation_open_long)
    }

    ComposableFlipperButton(
        modifier = modifier,
        text = text,
        color = LocalPallet.current.accentSecond,
        fapButtonSize = fapButtonSize,
        onClick = onClick
    )
}

@Composable
private fun ComposableFapOpeningButton(
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier,
) {
    val text = when (fapButtonSize) {
        FapButtonSize.COMPACTED -> stringResource(R.string.faphub_installation_open)
        FapButtonSize.LARGE -> stringResource(R.string.faphub_installation_open_long)
    }

    ComposableFlipperButton(
        modifier = modifier,
        text = text,
        color = LocalPallet.current.accentSecond,
        fapButtonSize = fapButtonSize,
    )
}
