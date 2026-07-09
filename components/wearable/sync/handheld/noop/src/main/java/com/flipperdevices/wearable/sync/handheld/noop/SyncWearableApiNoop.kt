package com.flipperdevices.wearable.sync.handheld.noop

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.wearable.sync.handheld.api.SyncWearableApi
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppGraph::class)
class SyncWearableApiNoop @Inject constructor() : SyncWearableApi {
    override suspend fun updateWearableIndex() = Unit
}
