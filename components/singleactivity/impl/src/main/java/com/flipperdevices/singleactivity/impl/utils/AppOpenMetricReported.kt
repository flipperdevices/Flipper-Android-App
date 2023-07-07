package com.flipperdevices.singleactivity.impl.utils

import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import javax.inject.Inject

private var appOpenReported = false

class AppOpenMetricReported @Inject constructor(
    private val metricApi: MetricApi
) {
    fun report() {
        if (!appOpenReported) {
            metricApi.reportSimpleEvent(SimpleEvent.APP_OPEN)
            appOpenReported = true
        }
    }
}
