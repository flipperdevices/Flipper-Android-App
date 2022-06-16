package com.flipperdevices.metric.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.metric.impl.CountlyApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, MetricApi::class)
class MetricApiImpl @Inject constructor(
    private val countlyApi: CountlyApi
) : MetricApi, LogTagProvider {
    override val TAG = "MetricApi"

    override fun reportSimpleEvent(simpleEvent: SimpleEvent) {
        try {
            countlyApi.reportEvent(simpleEvent.id)
        } catch (e: Exception) {
            error(e) { "Failed to report simple event: ${simpleEvent.id}" }
        }
    }

    override fun reportComplexEvent(complexEvent: ComplexEvent) {
        try {
            countlyApi.reportEvent(complexEvent.id, complexEvent.getParamsMap())
        } catch (e: Exception) {
            error(e) { "Failed to report simple event: $complexEvent" }
        }
    }
}
