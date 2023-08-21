package com.flipperdevices.wearable.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.flipperdevices.wearable.MainWearActivity
import com.flipperdevices.wearable.setup.api.SetupApi
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

@ContributesTo(AppGraph::class)
interface WearableComponent {
    val shake2report: Provider<Shake2ReportApi>
    val futureEntries: Provider<Set<AggregateFeatureEntry>>
    val composableFutureEntries: Provider<Set<ComposableFeatureEntry>>
    val setupApi: SetupApi

    fun inject(activity: MainWearActivity)
}
