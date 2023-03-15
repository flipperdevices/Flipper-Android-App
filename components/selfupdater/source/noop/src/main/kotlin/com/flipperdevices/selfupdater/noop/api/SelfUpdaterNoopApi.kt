package com.flipperdevices.selfupdater.noop.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterUIApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterUIApi::class)
class SelfUpdaterNoopApi @Inject constructor() : SelfUpdaterUIApi {
    @Composable
    override fun CheckAndShowUpdateDialog() = Unit
}
