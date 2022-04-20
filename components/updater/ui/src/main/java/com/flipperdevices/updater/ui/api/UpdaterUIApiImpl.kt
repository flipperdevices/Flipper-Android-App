package com.flipperdevices.updater.ui.api

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.UpdateCardApi
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.ui.composable.ComposableUpdateButton
import com.flipperdevices.updater.ui.viewmodel.UpdateCardViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class UpdaterUIApiImpl @Inject constructor() : UpdaterUIApi {

    @Composable
    override fun getUpdateCardApi(): UpdateCardApi {
        return viewModel<UpdateCardViewModel>()
    }

    @Composable
    override fun RenderUpdateButton(updateCardState: UpdateCardState) {
        ComposableUpdateButton(updateCardState)
    }
}
