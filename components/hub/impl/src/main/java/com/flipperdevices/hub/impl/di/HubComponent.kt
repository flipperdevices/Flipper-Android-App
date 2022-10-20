package com.flipperdevices.hub.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.hub.impl.fragments.HubFragment
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface HubComponent {
    fun inject(fragment: HubFragment)
}