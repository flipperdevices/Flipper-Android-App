package com.flipperdevices.app.di

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface MainComponent {
    val shake2report: Provider<Shake2ReportApi>
    val metricApi: Provider<MetricApi>
    val synchronizationApi: Provider<SynchronizationApi>
    val selfUpdaterApi: Provider<SelfUpdaterApi>
}
