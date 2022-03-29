package com.flipperdevices.bridge.synchronization.ui.api

import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SynchronizationUiApiImpl @Inject constructor() : SynchronizationUiApi
