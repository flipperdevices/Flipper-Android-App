package com.flipperdevices.analytics.shake2report.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.analytics.shake2report.impl.composable.states.ComposableReportError
import com.flipperdevices.analytics.shake2report.impl.composable.states.ComposableReportInProgress
import com.flipperdevices.analytics.shake2report.impl.composable.states.ComposableReportPending
import com.flipperdevices.analytics.shake2report.impl.composable.states.ComposableReportSuccessful
import com.flipperdevices.analytics.shake2report.impl.model.Shake2ReportState
import com.flipperdevices.analytics.shake2report.impl.viewmodel.Shake2ReportViewModel
import com.flipperdevices.core.ui.ktx.OrangeAppBar

@Composable
@Suppress("NonSkippableComposable")
fun Shake2ReportScreen(
    onBack: () -> Unit,
    viewModel: Shake2ReportViewModel,
    modifier: Modifier = Modifier,
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
            Shake2ReportState.Pending -> ComposableReportPending(onSubmit = viewModel::report)
            Shake2ReportState.Error -> ComposableReportError()
            Shake2ReportState.Uploading -> ComposableReportInProgress()
            is Shake2ReportState.Complete -> ComposableReportSuccessful(
                id = reportState.id,
                onCopyToClickBoard = viewModel::copyToClipboard
            )
        }
    }
}
