package com.flipperdevices.faphub.report.impl.composable.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.report.impl.R

@Composable
fun ComposableMainReport(
    onBack: () -> Unit,
    onOpenBug: () -> Unit,
    onOpenConcern: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
) {
    OrangeAppBar(titleId = R.string.fap_report_title, onBack = onBack)

    Card(modifier = Modifier.padding(14.dp)) {
        Column {
            ComposableReportItem(
                modifier = Modifier.clickableRipple(onClick = onOpenBug),
                textId = R.string.fap_report_main_report_bug,
                iconId = R.drawable.ic_bug
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LocalPallet.current.divider12)
            )
            ComposableReportItem(
                modifier = Modifier.clickableRipple(onClick = onOpenConcern),
                textId = R.string.fap_report_main_report_concern,
                iconId = R.drawable.ic_concern
            )
        }
    }
}

@Preview
@Composable
private fun ComposableMainReportPreview() {
    FlipperThemeInternal {
        ComposableMainReport(onBack = {}, onOpenBug = {}, onOpenConcern = {})
    }
}
