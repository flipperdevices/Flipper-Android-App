package com.flipperdevices.updater.card.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.updater.api.UpdaterCardApi
import com.flipperdevices.updater.card.composable.ComposableUpdaterCardInternal
import com.flipperdevices.updater.card.viewmodel.UpdateCardViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateRequestViewModel
import com.flipperdevices.updater.card.viewmodel.UpdateStateViewModel
import com.flipperdevices.updater.model.UpdateRequest
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class)
class UpdaterCardApiImpl @Inject constructor(
    private val updateCardFactory: UpdateCardViewModel.Factory,
    private val updateStateViewModelProvider: Provider<UpdateStateViewModel>,
    private val updateRequestViewModelProvider: Provider<UpdateRequestViewModel>
) : UpdaterCardApi {
    @Composable
    override fun ComposableUpdaterCard(
        modifier: Modifier,
        deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?,
        onStartUpdateRequest: (UpdateRequest) -> Unit,
        requestRefresh: Boolean,
        onRefreshRequestExecuted: () -> Unit
    ) {
        val updateStateViewModel: UpdateStateViewModel = viewModelWithFactory(key = null) {
            updateStateViewModelProvider.get()
        }
        val updateCardViewModel: UpdateCardViewModel = viewModelWithFactory(
            key = deeplink?.toString()
        ) {
            updateCardFactory(deeplink)
        }
        val updateRequestViewModel: UpdateRequestViewModel = viewModelWithFactory(key = null) {
            updateRequestViewModelProvider.get()
        }

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
            updateCardViewModel = updateCardViewModel,
            updateRequestViewModel = updateRequestViewModel
        )
    }
}
