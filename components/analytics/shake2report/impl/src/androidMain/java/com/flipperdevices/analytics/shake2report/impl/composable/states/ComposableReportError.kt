package com.flipperdevices.analytics.shake2report.impl.composable.states

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.core.markdown.ClickableUrlText
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ComposableReportError(
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    Text(
        text = stringResource(R.string.shake2report_error_post_title),
        style = LocalTypography.current.titleSB18,
        color = LocalPallet.current.text100
    )
    Image(
        modifier = Modifier.padding(bottom = 8.dp, top = 12.dp),
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                R.drawable.pic_flippper_error_red
            } else {
                R.drawable.pic_flippper_error_red_dark
            }
        ),
        contentDescription = null
    )
    ComposableReportDescription()
}

@Composable
private fun ColumnScope.ComposableReportDescription() {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ClickableUrlText(
            markdownResId = R.string.shake2report_error_post_forum,
            style = LocalTypography.current.bodyR14.copy(
                color = LocalPallet.current.text40
            ),
        )
        Text(
            text = stringResource(R.string.shake2report_check_bug),
            style = LocalTypography.current.bodyR14.copy(
                color = LocalPallet.current.text40
            ),
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableComposableShake2ReportReadyPreview() {
    FlipperThemeInternal {
        ComposableReportError()
    }
}
