package com.flipperdevices.firstpair.impl.composable.searching

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.firstpair.impl.R

private const val ICON_HORIZONTAL_PADDING_DP = 14
private const val ICON_SIZE = 24

@Composable
fun ComposableSearchingAppBar(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = ICON_HORIZONTAL_PADDING_DP.dp)
                .size(ICON_SIZE.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = onBack
                ),
            painter = painterResource(
                DesignSystem.drawable.ic_back
            ),
            contentDescription = stringResource(R.string.firstpair_search_back)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = (ICON_SIZE + ICON_HORIZONTAL_PADDING_DP * 2).dp),
            text = stringResource(R.string.firstpair_search_title),
            fontWeight = FontWeight.W700,
            color = colorResource(DesignSystem.color.black_100),
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}
