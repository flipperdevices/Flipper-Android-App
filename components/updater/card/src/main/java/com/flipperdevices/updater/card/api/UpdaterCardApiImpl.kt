package com.flipperdevices.updater.card.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
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
    @Suppress("NonSkippableComposable")
    override fun ComposableUpdaterCard(
        modifier: Modifier,
        componentContext: ComponentContext,
        deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?,
        onStartUpdateRequest: (UpdateRequest) -> Unit,
        requestRefresh: Boolean,
        onRefreshRequestExecute: () -> Unit
    ) {
        val updateStateViewModel: UpdateStateViewModel =
            componentContext.viewModelWithFactory(key = null) {
                updateStateViewModelProvider.get()
            }
        val updateCardViewModel: UpdateCardViewModel = componentContext.viewModelWithFactory(
            key = deeplink?.toString()
        ) {
            updateCardFactory(deeplink)
        }
        val updateRequestViewModel: UpdateRequestViewModel =
            componentContext.viewModelWithFactory(key = null) {
                updateRequestViewModelProvider.get()
            }

        LaunchedEffect(requestRefresh, onRefreshRequestExecute) {
            if (requestRefresh) {
                updateCardViewModel.refresh()
                onRefreshRequestExecute()
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
