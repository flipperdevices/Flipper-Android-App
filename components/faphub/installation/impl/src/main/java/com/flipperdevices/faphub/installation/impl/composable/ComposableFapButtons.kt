package com.flipperdevices.faphub.installation.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.installation.impl.R

@Composable
fun ComposableFapInstallationButton(
    modifier: Modifier,
    textSize: TextUnit
) {
    ComposableFlipperButton(
        modifier = modifier,
        text = stringResource(R.string.faphub_installation_install),
        color = LocalPallet.current.accent,
        fontSize = textSize
    )
}

@Composable
fun ComposableFapUpdateButton(
    modifier: Modifier,
    textSize: TextUnit
) {
    ComposableFlipperButton(
        modifier = modifier,
        text = stringResource(R.string.faphub_installation_update),
        color = LocalPallet.current.updateProgressGreen,
        fontSize = textSize
    )
}