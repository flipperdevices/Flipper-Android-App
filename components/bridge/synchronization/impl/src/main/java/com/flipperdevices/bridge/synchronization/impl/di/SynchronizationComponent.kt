package com.flipperdevices.bridge.synchronization.impl.di

import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepository
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface SynchronizationComponent {
    fun inject(repository: ManifestRepository)
}
