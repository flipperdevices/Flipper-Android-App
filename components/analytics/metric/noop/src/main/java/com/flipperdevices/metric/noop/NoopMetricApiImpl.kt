package com.flipperdevices.metric.noop

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SimpleEvent
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, MetricApi::class)
class NoopMetricApiImpl @Inject constructor() : MetricApi {
    override fun reportSimpleEvent(simpleEvent: SimpleEvent, arg: String?) = Unit

    override fun reportComplexEvent(complexEvent: ComplexEvent) = Unit
}
