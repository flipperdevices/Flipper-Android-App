package com.flipperdevices.singleactivity.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.singleactivity.impl.SingleActivity
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppGraph::class)
interface SingleActivityComponent {
    fun inject(activity: SingleActivity)
}
