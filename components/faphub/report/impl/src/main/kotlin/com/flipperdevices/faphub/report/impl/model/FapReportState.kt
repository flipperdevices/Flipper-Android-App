package com.flipperdevices.faphub.report.impl.model

sealed class FapReportState {
    object ReadyToReport : FapReportState()

    object Uploading : FapReportState()
}
