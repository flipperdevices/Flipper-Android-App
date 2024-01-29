package com.flipperdevices.wearable.sync.handheld.noop

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.wearable.sync.handheld.api.SyncWearableApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SyncWearableApiNoop @Inject constructor() : SyncWearableApi {
    override suspend fun updateWearableIndex() = Unit
}
