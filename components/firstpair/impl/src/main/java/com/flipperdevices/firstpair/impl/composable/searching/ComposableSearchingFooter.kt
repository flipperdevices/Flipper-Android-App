package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.firstpair.impl.R

@Composable
fun ComposableSearchingFooter(
    onClickSkipConnection: () -> Unit
) {
    Text(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClickSkipConnection
            )
            .padding(all = 8.dp)
            .fillMaxWidth(),
        text = stringResource(R.string.firstpair_search_skip_connection),
        color = LocalPallet.current.accentSecond,
        style = LocalTypography.current.buttonM16,
        textAlign = TextAlign.Center
    )
}
