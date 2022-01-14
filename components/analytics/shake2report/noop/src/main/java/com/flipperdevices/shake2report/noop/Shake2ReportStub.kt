package com.flipperdevices.shake2report.noop

import android.app.Application
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class Shake2ReportStub @Inject constructor() : Shake2ReportApi {
    override fun init(application: Application) {
        // Do nothing
    }

    override fun reportException(throwable: Throwable, tag: String?, extras: Map<String, String>?) {
        // Do nothing
    }
}
