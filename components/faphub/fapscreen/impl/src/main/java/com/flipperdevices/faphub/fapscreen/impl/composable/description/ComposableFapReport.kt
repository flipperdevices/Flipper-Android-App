package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.fapscreen.impl.R

@Composable
fun ComposableFapReport(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .clickableRipple(onClick = onClick)
        .then(modifier),
    verticalAlignment = Alignment.CenterVertically
) {
    val text = stringResource(R.string.fapscreen_developer_report)
    Icon(
        modifier = Modifier.size(24.dp),
        painter = painterResource(R.drawable.ic_report),
        contentDescription = text,
        tint = LocalPallet.current.warningColor
    )
    Text(
        modifier = Modifier
            .padding(start = 8.dp),
        text = text,
        style = LocalTypography.current.bodyR14,
        color = LocalPallet.current.warningColor
    )
}
