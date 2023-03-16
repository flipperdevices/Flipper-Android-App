package com.flipperdevices.selfupdater.googleplay.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterUIApi
import com.flipperdevices.selfupdater.googleplay.viewmodel.GooglePlayUpdaterViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import tangle.viewmodel.compose.tangleViewModel

@ContributesBinding(AppGraph::class, SelfUpdaterUIApi::class)
class SelfUpdaterGooglePlayApi @Inject constructor(
) : SelfUpdaterUIApi {
    @Composable
    override fun CheckAndShowUpdateDialog() {
        val viewModel = tangleViewModel<GooglePlayUpdaterViewModel>()
    }
}
