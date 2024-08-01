package com.flipperdevices.analytics.shake2report.impl.api

import com.flipperdevices.analytics.shake2report.impl.InternalShake2Report
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
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

    override fun setExtra(tags: List<Pair<String, String>>) {
        internalShake2Report.setExtra(tags)
    }

    override fun reportException(throwable: Throwable, tag: String?, extras: Map<String, String>?) {
        val event = SentryEvent(throwable)
        extras?.let { event.setExtras(extras) }
        tag?.let { event.setTag("source", it) }

        Sentry.captureEvent(event)
    }

    override fun isInitialized() = internalShake2Report.getIsRegisteredFlow()
}
