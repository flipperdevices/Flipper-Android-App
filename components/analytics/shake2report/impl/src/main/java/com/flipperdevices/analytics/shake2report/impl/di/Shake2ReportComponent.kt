package com.flipperdevices.analytics.shake2report.impl.di

import com.flipperdevices.analytics.shake2report.impl.activity.Shake2ReportActivity
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface Shake2ReportComponent {
    fun inject(activity: Shake2ReportActivity)
}
