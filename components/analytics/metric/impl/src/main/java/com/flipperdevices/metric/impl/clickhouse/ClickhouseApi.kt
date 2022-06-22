package com.flipperdevices.metric.impl.clickhouse

import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SimpleEvent

interface ClickhouseApi {
    fun reportSimpleEvent(simpleEvent: SimpleEvent)
    fun reportComplexEvent(complexEvent: ComplexEvent)
}
