package com.flipperdevices.selfupdater.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.impl.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.impl.compose.ComposableSelfUpdate
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SelfUpdaterApiImpl @Inject constructor() : SelfUpdaterApi {
    @Composable
    override fun CheckAndShowUpdateDialog() {
        ComposableSelfUpdate()
    }
}
