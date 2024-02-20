package com.flipperdevices.firstpair.impl.composable.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val ICON_HORIZONTAL_PADDING_DP = 14
private const val ICON_SIZE = 24

@Composable
fun ComposableSearchingAppBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalPallet.current.background)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = ICON_HORIZONTAL_PADDING_DP.dp)
                .size(ICON_SIZE.dp)
                .clickableRipple(bounded = false, onClick = onBack),
            painter = painterResource(DesignSystem.drawable.ic_back),
            contentDescription = stringResource(R.string.firstpair_search_back)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = (ICON_SIZE + ICON_HORIZONTAL_PADDING_DP * 2).dp),
            text = title,
            style = LocalTypography.current.titleB22,
            textAlign = TextAlign.Center
        )
    }
}
