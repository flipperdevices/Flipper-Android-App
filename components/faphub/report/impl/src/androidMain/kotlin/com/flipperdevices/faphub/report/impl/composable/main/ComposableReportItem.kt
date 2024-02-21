package com.flipperdevices.faphub.report.impl.composable.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.report.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableReportItem(
    @StringRes textId: Int,
    @DrawableRes iconId: Int,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        modifier = Modifier
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
            .size(18.dp),
        painter = painterResource(iconId),
        contentDescription = stringResource(textId)
    )
    Text(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .weight(1f),
        text = stringResource(textId),
        style = LocalTypography.current.bodyM14
    )
    Icon(
        modifier = Modifier
            .padding(bottom = 12.dp, top = 12.dp, end = 12.dp)
            .size(16.dp),
        painter = painterResource(DesignSystem.drawable.ic_forward),
        contentDescription = null
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableReportItemPreview() {
    FlipperThemeInternal {
        ComposableReportItem(
            textId = R.string.fap_report_main_report_bug,
            iconId = R.drawable.ic_bug
        )
    }
}
