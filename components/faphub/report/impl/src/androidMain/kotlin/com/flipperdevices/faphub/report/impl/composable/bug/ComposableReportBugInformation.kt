package com.flipperdevices.faphub.report.impl.composable.bug

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.markdown.ComposableMarkdown
import com.flipperdevices.core.markdown.markdownTypography
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.report.impl.R

@Composable
fun ComposableReportBugInformation(
    onClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier.fillMaxSize()) {
    OrangeAppBar(titleId = R.string.fap_report_title, onBack = onBack)

    ComposableReportBugInformationContent(
        onClick = onClick
    )
}

@Composable
fun ComposableReportBugInformationContent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.padding(vertical = 24.dp, horizontal = 14.dp)
) {
    Text(
        text = stringResource(R.string.fap_report_instruction_title),
        style = LocalTypography.current.buttonM16
    )

    Image(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        painter = painterResource(R.drawable.pic_github_instruction),
        contentDescription = stringResource(R.string.fap_report_instruction_title),
        contentScale = ContentScale.FillWidth
    )

    ComposableMarkdown(
        content = stringResource(R.string.fap_report_instruction_desc),
        typography = markdownTypography(
            textStyle = LocalTypography.current.bodyR14
        )
    )

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_github),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.fap_report_instruction_btn),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text100,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableReportBugInformationPreview() {
    FlipperThemeInternal {
        ComposableReportBugInformation(onBack = {}, onClick = {})
    }
}
