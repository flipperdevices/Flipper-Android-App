package com.flipperdevices.metric.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.metric.impl.clickhouse.ClickhouseApi
import com.flipperdevices.metric.impl.countly.CountlyApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, MetricApi::class)
class MetricApiImpl @Inject constructor(
    private val countlyApi: CountlyApi,
    private val clickhouseApi: ClickhouseApi
) : MetricApi, LogTagProvider {
    override val TAG = "MetricApi"

    override fun reportSimpleEvent(simpleEvent: SimpleEvent) {
        try {
            countlyApi.reportEvent(simpleEvent.id)
        } catch (e: Exception) {
            error(e) { "Failed to report to Countly simple event: ${simpleEvent.id}" }
        }
        try {
            clickhouseApi.reportSimpleEvent(simpleEvent)
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
}
