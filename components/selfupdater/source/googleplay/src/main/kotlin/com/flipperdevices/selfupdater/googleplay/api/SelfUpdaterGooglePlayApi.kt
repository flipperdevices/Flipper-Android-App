package com.flipperdevices.selfupdater.googleplay.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterUIApi
import com.flipperdevices.selfupdater.googleplay.composable.ComposablePendingUpdateDialog
import com.flipperdevices.selfupdater.googleplay.composable.ComposableReadyUpdateDialog
import com.flipperdevices.selfupdater.googleplay.viewmodel.GooglePlayUpdaterViewModel
import com.flipperdevices.selfupdater.googleplay.viewmodel.UpdateState
import com.squareup.anvil.annotations.ContributesBinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterUIApi::class)
class SelfUpdaterGooglePlayApi @Inject constructor() : SelfUpdaterUIApi {
    @Composable
    override fun CheckAndShowUpdateDialog() {
        val viewModel = tangleViewModel<GooglePlayUpdaterViewModel>()
        val state by viewModel.getUpdateState().collectAsState()

        when (state) {
            UpdateState.AVAILABLE -> ComposablePendingUpdateDialog(
                onAccept = viewModel::requestDownloadUpdate,
                onDecline = viewModel::declineUpdate
            )
            UpdateState.DOWNLOADED -> ComposableReadyUpdateDialog(
                onAccept = viewModel::startUpdate,
                onDecline = viewModel::declineUpdate
            )
            else -> {}
        }
    }
}
