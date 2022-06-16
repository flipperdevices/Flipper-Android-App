package com.flipperdevices.metric.api

import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SimpleEvent

interface MetricApi {
    fun reportSimpleEvent(simpleEvent: SimpleEvent)
    fun reportComplexEvent(complexEvent: ComplexEvent)
}
