package com.flipperdevices.metric.noop

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.metric.api.MetricAndroidApi
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SessionState
import com.flipperdevices.metric.api.events.SimpleEvent
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<MetricAndroidApi>())
class NoopMetricAndroidApiImpl @Inject constructor() : MetricApi, MetricAndroidApi {
    override fun reportSessionState(state: SessionState) = Unit

    override fun reportSimpleEvent(simpleEvent: SimpleEvent, arg: String?) = Unit

    override fun reportComplexEvent(complexEvent: ComplexEvent) = Unit
}
