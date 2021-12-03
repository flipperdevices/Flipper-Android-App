package com.flipperdevices.bridge.synchronization.impl.api

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding

@ContributesBinding(AppGraph::class)
class SynchronizationApiImpl : SynchronizationApi {
    override fun startSynchronization(force: Boolean) {
    }
}
