package com.flipperdevices.analytics.shake2report.impl.composable.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.core.ui.ktx.elements.animatedDots
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
internal fun ComposableReportInProgress(
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    CircularProgressIndicator(
        color = LocalPallet.current.accentSecond
    )
    Text(
        modifier = Modifier.padding(16.dp),
        text = stringResource(R.string.shake2report_progress_uploading) + animatedDots(),
        textAlign = TextAlign.Center
    )
}
