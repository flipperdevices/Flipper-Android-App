package com.flipperdevices.shake2report.noop

import android.content.Context
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.github.terrakok.cicerone.Screen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class Shake2ReportStub @Inject constructor() : Shake2ReportApi {
    override fun init() {
        // Do nothing
    }

    override fun reportBugScreen(context: Context): Screen? = null

    override fun updateGattInformation(gattInformation: FlipperGATTInformation) {
        // Do nothing
    }

    override fun setExtra(tags: List<Pair<String, String>>) {
        // Do nothing
    }

    override fun reportException(throwable: Throwable, tag: String?, extras: Map<String, String>?) {
        // Do nothing
    }
}
