package com.flipperdevices.metric.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.metric.api.MetricAndroidApi
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SessionState
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.metric.impl.clickhouse.ClickhouseApi
import com.flipperdevices.metric.impl.countly.CountlyApi
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@SingleIn(AppGraph::class)
@ContributesBinding(AppGraph::class, binding<MetricApi>())
@ContributesBinding(AppGraph::class, binding<MetricAndroidApi>())
class MetricApiImpl @Inject constructor(
    private val countlyApi: CountlyApi,
    private val clickhouseApi: ClickhouseApi
) : MetricApi, MetricAndroidApi, LogTagProvider {
    override val TAG = "MetricApi"

    override fun reportSimpleEvent(simpleEvent: SimpleEvent, arg: String?) {
        try {
            countlyApi.reportEvent(simpleEvent.id, params = mapOf("arg" to arg))
        } catch (e: Exception) {
            error(e) { "Failed to report to Countly simple event: ${simpleEvent.id}" }
        }
        try {
            clickhouseApi.reportSimpleEvent(simpleEvent, simpleEventArg = arg)
        } catch (e: Exception) {
            error(e) { "Failed to report to Clickhouse simple event: ${simpleEvent.id}" }
        }
    }

    override fun reportComplexEvent(complexEvent: ComplexEvent) {
        try {
            countlyApi.reportEvent(complexEvent.id, complexEvent.getParamsMap())
        } catch (e: Exception) {
            error(e) { "Failed to report to Countly simple event: $complexEvent" }
        }
        try {
            clickhouseApi.reportComplexEvent(complexEvent)
        } catch (e: Exception) {
            error(e) { "Failed to report to Clickhouse simple event: $complexEvent" }
        }
    }

    override fun reportSessionState(state: SessionState) {
        countlyApi.reportSessionState(state)
    }
}
