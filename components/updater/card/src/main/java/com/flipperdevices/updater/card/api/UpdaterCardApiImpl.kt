package com.flipperdevices.updater.card.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.UpdaterCardApi
import com.flipperdevices.updater.card.composable.ComposableUpdaterCardInternal
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateStateViewModel
import com.flipperdevices.updater.model.UpdateRequest
import com.squareup.anvil.annotations.ContributesBinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class UpdaterCardApiImpl @Inject constructor() : UpdaterCardApi {
    @Composable
    override fun ComposableUpdaterCard(
        modifier: Modifier,
        onStartUpdateRequest: (UpdateRequest) -> Unit,
        requestRefresh: Boolean,
        onRefreshRequestExecuted: () -> Unit
    ) {
        val updateStateViewModel: UpdateStateViewModel = tangleViewModel()
        val updateCardViewModel: UpdateCardViewModel = tangleViewModel()

        LaunchedEffect(requestRefresh) {
            if (requestRefresh) {
                updateCardViewModel.refresh()
                onRefreshRequestExecuted()
            }
        }

        ComposableUpdaterCardInternal(
            modifier = modifier,
            onStartUpdateRequest = onStartUpdateRequest,
            updateStateViewModel = updateStateViewModel,
            updateCardViewModel = updateCardViewModel
        )
    }
}
