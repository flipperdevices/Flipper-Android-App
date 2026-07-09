package com.flipperdevices.wearable.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.flipperdevices.wearable.MainWearActivity
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provider

@ContributesTo(AppGraph::class)
interface WearableComponent {
    val shake2report: Provider<Shake2ReportApi>

    fun inject(activity: MainWearActivity)
}
