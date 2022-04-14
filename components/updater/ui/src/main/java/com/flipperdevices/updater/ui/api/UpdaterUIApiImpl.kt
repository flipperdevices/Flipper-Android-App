package com.flipperdevices.updater.ui.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.ui.composable.ComposableUpdater
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class UpdaterUIApiImpl @Inject constructor() : UpdaterUIApi {
    @Composable
    override fun ComposableUpdateCardContent() {
        ComposableUpdater()
    }
}
