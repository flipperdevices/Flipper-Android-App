package com.flipperdevices.analytics.shake2report.impl.composable.states

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.elements.ComposableReportTextField
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.res.R as SharedResources

@Composable
internal fun ComposableReportSuccessful(
    id: String,
    onCopyToClickBoard: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    Text(
        text = stringResource(R.string.shake2report_post_forum_title),
        style = LocalTypography.current.titleSB18,
        color = LocalPallet.current.text100
    )
    Image(
        modifier = Modifier.padding(bottom = 8.dp, top = 12.dp),
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                SharedResources.drawable.pic_flippper_successfull_green
            } else {
                SharedResources.drawable.pic_flippper_successfull_green_dark
            }
        ),
        contentDescription = null
    )
    ComposableReportIssueId(id = id, onCopyToClickBoard = onCopyToClickBoard)
    ComposableReportDescription()
}

@Composable
private fun ComposableReportIssueId(
    id: String,
    onCopyToClickBoard: () -> Unit,
) {
    ComposableReportTextField(
        value = "",
        title = {
            Text(
                text = stringResource(R.string.shake2report_issue_id),
                style = LocalTypography.current.buttonB16.copy(
                    color = LocalPallet.current.text40
                ),
            )
        },
        onValueChange = {},
        enabled = false,
        maxLines = 1,
        placeholder = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = id,
                    style = LocalTypography.current.bodyR14.copy(
                        color = LocalPallet.current.text100
                    )
                )
                Text(
                    modifier = Modifier.clickableRipple(onClick = onCopyToClickBoard),
                    text = stringResource(R.string.shake2report_copy),
                    style = LocalTypography.current.bodyR14.copy(
                        color = LocalPallet.current.accentSecond
                    )
                )
            }
        },
    )
}

@Composable
private fun ColumnScope.ComposableReportDescription() {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ClickableUrlText(
            markdownResId = R.string.shake2report_post_forum,
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
        ComposableReportSuccessful(
            id = "1f5c7f2a9a554f2d982fe46a6e3ed9e0",
            onCopyToClickBoard = {}
        )
    }
}
