package com.flipperdevices.bridge.impl.di

import com.flipperdevices.bridge.impl.manager.service.FlipperInformationApiImpl
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface BridgeImplComponent {
    fun inject(informationApi: FlipperInformationApiImpl)
}
