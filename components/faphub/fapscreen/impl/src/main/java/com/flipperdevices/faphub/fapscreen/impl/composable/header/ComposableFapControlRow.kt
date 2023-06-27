package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R
import com.flipperdevices.faphub.fapscreen.impl.model.FapDetailedControlState

@Composable
fun ComposableFapControlRow(
    controlState: FapDetailedControlState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    installationButton: @Composable (FapItem?, Modifier) -> Unit
) {
    Crossfade(targetState = controlState) { controlStateLocal ->
        when (controlStateLocal) {
            is FapDetailedControlState.InProgressOrNotInstalled -> ComposableFapControlRowInProgress(
                modifier = modifier,
                state = controlStateLocal,
                installationButton = installationButton,
            )

            is FapDetailedControlState.Installed -> ComposableFapControlRowInstalled(
                modifier = modifier,
                state = controlStateLocal,
                installationButton = installationButton,
                onDelete = onDelete
            )

            FapDetailedControlState.Loading -> ComposableFapControlRowInProgress(modifier)
        }
    }
}

@Composable
private fun ComposableFapControlRowInProgress(
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
        .fillMaxWidth()
        .height(46.dp)
        .placeholderConnecting()
)

@Composable
private fun ComposableFapControlRowInProgress(
    state: FapDetailedControlState.InProgressOrNotInstalled,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    installationButton(
        state.fapItem,
        Modifier
            .weight(weight = 1f)
            .fillMaxHeight()
    )
}

@Composable
private fun ComposableFapControlRowInstalled(
    state: FapDetailedControlState.Installed,
    installationButton: @Composable (FapItem?, Modifier) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        modifier = Modifier
            .padding(end = 12.dp)
            .size(46.dp)
            .clickableRipple(onClick = onDelete),
        painter = painterResource(R.drawable.ic_delete),
        contentDescription = stringResource(R.string.fapscreen_install_delete_desc),
        tint = LocalPallet.current.onError
    )
    installationButton(
        state.fapItem,
        Modifier
            .weight(weight = 1f)
            .fillMaxHeight()
    )
}
