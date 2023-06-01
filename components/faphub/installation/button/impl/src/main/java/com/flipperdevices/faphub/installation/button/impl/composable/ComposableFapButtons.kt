package com.flipperdevices.faphub.installation.button.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.R

@Composable
fun ComposableFapInstallButton(
    fapButtonSize: FapButtonSize,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComposableFlipperButton(
        modifier = modifier,
        text = stringResource(R.string.faphub_installation_install),
        color = LocalPallet.current.accent,
        fapButtonSize = fapButtonSize,
        onClick = onClick
    )
}

@Composable
fun ComposableFapInstalledButton(
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier
) {
    ComposableFlipperButton(
        modifier = modifier,
        text = stringResource(R.string.faphub_installation_installed),
        color = LocalPallet.current.text20,
        fapButtonSize = fapButtonSize
    )
}

@Composable
fun ComposableFapUpdateButton(
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ComposableFlipperButton(
        modifier = modifier,
        text = stringResource(R.string.faphub_installation_update),
        color = LocalPallet.current.updateProgressGreen,
        fapButtonSize = fapButtonSize,
        onClick = onClick
    )
}

@Composable
fun ComposableFapInstallingButton(
    percent: Float,
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier
) {
    ComposableDynamicFlipperButton(
        modifier = modifier,
        percent = percent,
        color = LocalPallet.current.accent,
        fapButtonSize = fapButtonSize
    )
}

@Composable
fun ComposableFapUpdatingButton(
    percent: Float,
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier
) {
    ComposableDynamicFlipperButton(
        modifier = modifier,
        percent = percent,
        color = LocalPallet.current.updateProgressGreen,
        fapButtonSize = fapButtonSize
    )
}

@Composable
fun ComposableFapCancelingButton(
    fapButtonSize: FapButtonSize,
    modifier: Modifier = Modifier
) {
    ComposableFlipperButton(
        modifier = modifier,
        text = stringResource(R.string.faphub_installation_canceling),
        color = LocalPallet.current.updateProgressGreen,
        fapButtonSize = fapButtonSize
    )
}

@Preview
@Composable
private fun ComposableFapInstallButtonPreview() {
    FlipperThemeInternal {
        Column {
            ComposableFapInstallButton(fapButtonSize = FapButtonSize.LARGE, onClick = {})
            ComposableFapInstalledButton(fapButtonSize = FapButtonSize.LARGE)
            ComposableFapUpdateButton(fapButtonSize = FapButtonSize.LARGE, onClick = {})
            ComposableFapInstallingButton(percent = 0.5f, fapButtonSize = FapButtonSize.LARGE)
            ComposableFapUpdatingButton(percent = 0.5f, fapButtonSize = FapButtonSize.LARGE)
        }
    }
}
