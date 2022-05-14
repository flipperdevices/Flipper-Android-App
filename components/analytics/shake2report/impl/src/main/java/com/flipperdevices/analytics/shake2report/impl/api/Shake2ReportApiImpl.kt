package com.flipperdevices.analytics.shake2report.impl.api

import android.content.Context
import android.content.Intent
import com.flipperdevices.analytics.shake2report.impl.InternalShake2Report
import com.flipperdevices.analytics.shake2report.impl.activity.Shake2ReportActivity
import com.flipperdevices.analytics.shake2report.impl.helper.FlipperInformationMapping
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.squareup.anvil.annotations.ContributesBinding
import io.sentry.Sentry
import io.sentry.SentryEvent
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class Shake2ReportApiImpl @Inject constructor(
    private val internalShake2Report: InternalShake2Report
) : Shake2ReportApi {
    override fun init() {
        internalShake2Report.register()
    }

    override fun reportBugScreen(context: Context): Screen {
        return ActivityScreen { Intent(context, Shake2ReportActivity::class.java) }
    }

    override fun updateGattInformation(gattInformation: FlipperGATTInformation) {
        internalShake2Report.setExtra(FlipperInformationMapping.convert(gattInformation))
    }

    override fun updateRpcInformation(rpcInformation: FlipperRpcInformation) {
        internalShake2Report.setExtra(FlipperInformationMapping.convert(rpcInformation))
    }

    override fun reportException(throwable: Throwable, tag: String?, extras: Map<String, String>?) {
        val event = SentryEvent(throwable)
        extras?.let { event.setExtras(extras) }
        tag?.let { event.setTag("source", it) }

        Sentry.captureEvent(event)
    }
}
