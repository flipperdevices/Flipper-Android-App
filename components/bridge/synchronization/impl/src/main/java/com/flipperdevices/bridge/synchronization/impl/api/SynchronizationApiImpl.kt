package com.flipperdevices.bridge.synchronization.impl.api

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.impl.TmpSynchronization
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SynchronizationApiImpl @Inject constructor() : SynchronizationApi {
    val synchronization by lazy { TmpSynchronization() }
    override fun startSynchronization(force: Boolean) {
        synchronization.requestServiceAndReceive()
    }
}
