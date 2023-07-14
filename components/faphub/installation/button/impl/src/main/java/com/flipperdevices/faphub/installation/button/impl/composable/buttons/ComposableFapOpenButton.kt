package com.flipperdevices.faphub.installation.button.impl.composable.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.impl.R
import com.flipperdevices.faphub.installation.button.impl.composable.elements.ComposableFlipperButton

@Composable
internal fun ComposableFapOpenButton(
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
