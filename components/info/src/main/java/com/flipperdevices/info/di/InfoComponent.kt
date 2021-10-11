package com.flipperdevices.info.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.main.InfoFragment
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface InfoComponent {
    fun inject(fragment: InfoFragment)
}
