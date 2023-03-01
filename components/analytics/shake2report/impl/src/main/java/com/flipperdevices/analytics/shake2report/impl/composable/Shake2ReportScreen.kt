package com.flipperdevices.analytics.shake2report.impl.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.analytics.shake2report.impl.model.Shake2ReportState
import com.flipperdevices.analytics.shake2report.impl.viewmodel.Shake2ReportViewModel
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.ktx.animatedDots
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun Shake2ReportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: Shake2ReportViewModel = tangleViewModel()
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    OrangeAppBar(
        titleId = R.string.shake2report_title,
        onBack = onBack
    )
    val state by viewModel.getState().collectAsState()
    state.let { reportState ->
        when (reportState) {
            Shake2ReportState.Pending -> ComposableReportScreen(onSubmit = viewModel::report)
            Shake2ReportState.Compressing ->
                ComposableShake2ReportInProgress(R.string.shake2report_progress_compressing)
            Shake2ReportState.Uploading ->
                ComposableShake2ReportInProgress(R.string.shake2report_progress_uploading)
            is Shake2ReportState.Complete -> ComposableShake2ReportReady(
                id = reportState.id,
                onCopyToClickBoard = viewModel::copyToClipboard,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun ComposableShake2ReportInProgress(
    @StringRes titleId: Int,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    CircularProgressIndicator()
    Text(
        modifier = Modifier.padding(16.dp),
        text = stringResource(titleId) + animatedDots(),
        textAlign = TextAlign.Center
    )
}
