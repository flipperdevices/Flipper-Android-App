package com.flipperdevices.core.ui.errors

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableNoNetworkError(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Image(
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                DesignSystem.drawable.ic_no_internet
            } else {
                DesignSystem.drawable.ic_no_internet_dark
            }
        ),
        contentDescription = stringResource(R.string.common_error_no_network_title)
    )

    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = stringResource(R.string.common_error_no_network_title),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text100,
        textAlign = TextAlign.Center
    )
    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = stringResource(R.string.common_error_no_network_desc),
        style = LocalTypography.current.subtitleM12,
        color = LocalPallet.current.text40,
        textAlign = TextAlign.Center
    )

    Text(
        modifier = Modifier.padding(top = 12.dp)
            .clickable(onClick = onRetry),
        text = stringResource(R.string.common_error_no_network_btn),
        style = LocalTypography.current.bodyM14,
        color = LocalPallet.current.accentSecond,
        textAlign = TextAlign.Center
    )
}
