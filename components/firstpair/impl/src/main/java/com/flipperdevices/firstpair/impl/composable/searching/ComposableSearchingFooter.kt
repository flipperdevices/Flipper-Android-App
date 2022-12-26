package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.firstpair.impl.R

@Composable
fun ComposableSearchingFooter(
    onClickSkipConnection: () -> Unit
) {
    Text(
        modifier = Modifier
            .clickableRipple(onClick = onClickSkipConnection)
            .padding(all = 8.dp)
            .fillMaxWidth(),
        text = stringResource(R.string.firstpair_search_skip_connection),
        color = LocalPallet.current.accentSecond,
        style = LocalTypography.current.buttonM16,
        textAlign = TextAlign.Center
    )
}
