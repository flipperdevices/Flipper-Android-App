package com.flipperdevices.analytics.shake2report.impl.model

sealed class Shake2ReportState {
    object Pending : Shake2ReportState()
    object Error : Shake2ReportState()
    object Uploading : Shake2ReportState()
    data class Complete(val id: String) : Shake2ReportState()
}
