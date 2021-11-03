package com.flipperdevices.shake2report.noop

import android.app.Application
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesBinding

@ContributesBinding(AppGraph::class)
class Shake2ReportStub : Shake2ReportApi {
    override fun init(application: Application) {
        // Do nothing
    }
}
