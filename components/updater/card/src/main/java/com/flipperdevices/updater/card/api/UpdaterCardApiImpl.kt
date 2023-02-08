package com.flipperdevices.updater.card.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.UpdaterCardApi
import com.flipperdevices.updater.card.composable.ComposableUpdaterCardInternal
import com.flipperdevices.updater.model.UpdateRequest
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class UpdaterCardApiImpl @Inject constructor() : UpdaterCardApi {
    @Composable
    override fun ComposableUpdaterCard(modifier: Modifier, onStartUpdateRequest: (UpdateRequest) -> Unit) {
        ComposableUpdaterCardInternal(modifier, onStartUpdateRequest = onStartUpdateRequest)
    }
}
