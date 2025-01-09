package com.flipperdevices.metric.noop

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.metric.api.MetricAndroidApi
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.ComplexEvent
import com.flipperdevices.metric.api.events.SessionState
import com.flipperdevices.metric.api.events.SimpleEvent
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, MetricAndroidApi::class)
class NoopMetricAndroidApiImpl @Inject constructor() : MetricApi, MetricAndroidApi {
    override fun reportSessionState(state: SessionState) = Unit

    override fun reportSimpleEvent(simpleEvent: SimpleEvent, arg: String?) = Unit

    override fun reportComplexEvent(complexEvent: ComplexEvent) = Unit
}
