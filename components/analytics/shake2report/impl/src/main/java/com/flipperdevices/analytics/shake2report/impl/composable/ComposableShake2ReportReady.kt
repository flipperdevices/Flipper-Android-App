package com.flipperdevices.analytics.shake2report.impl.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalTypography
import com.mikepenz.markdown.compose.Markdown
import com.flipperdevices.core.ui.res.R as SharedResources

@Composable
fun ComposableShake2ReportReady(
    id: String,
    onCopyToClickBoard: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(top = 32.dp, start = 16.dp, end = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    Image(
        painter = painterResource(
            if (MaterialTheme.colors.isLight) {
                SharedResources.drawable.pic_update_successfull
            } else {
                SharedResources.drawable.pic_update_successfull_dark
            }
        ),
        contentDescription = null
    )
    Text(
        text = stringResource(R.string.shake2report_submit_title),
        textAlign = TextAlign.Center,
        style = LocalTypography.current.titleB18
    )
    Markdown(
        modifier = Modifier.fillMaxWidth(),
        content = stringResource(R.string.shake2report_submit_desc)
    )
    val clickText = stringResource(R.string.shake2report_submit_click)
    Text(
        modifier = Modifier
            .clickable { onCopyToClickBoard(id) }
            .weight(1f),
        text = buildAnnotatedString {
            append(clickText)
            append(' ')
            withStyle(LocalTypography.current.monoSpaceM14.toSpanStyle()) {
                append(id)
            }
        }
    )
    ComposableFlipperButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        text = stringResource(R.string.shake2report_submit_btn),
        onClick = onBack
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableComposableShake2ReportReadyPreview() {
    FlipperThemeInternal {
        ComposableShake2ReportReady(
            id = "1f5c7f2a9a554f2d982fe46a6e3ed9e0",
            onCopyToClickBoard = {},
            onBack = {}
        )
    }
}
