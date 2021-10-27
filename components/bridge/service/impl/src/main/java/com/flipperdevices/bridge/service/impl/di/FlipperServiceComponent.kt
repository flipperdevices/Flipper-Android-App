package com.flipperdevices.bridge.service.impl.di

import com.flipperdevices.bridge.service.impl.FlipperServiceApiImpl
import com.flipperdevices.bridge.service.impl.delegate.FlipperServiceConnectDelegate
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface FlipperServiceComponent {
    fun inject(delegate: FlipperServiceConnectDelegate)
    fun inject(serviceApi: FlipperServiceApiImpl)
}
