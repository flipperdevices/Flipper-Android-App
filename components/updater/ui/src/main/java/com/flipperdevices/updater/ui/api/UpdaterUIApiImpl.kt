package com.flipperdevices.updater.ui.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.ui.viewmodel.UpdaterViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class UpdaterUIApiImpl @Inject constructor() : UpdaterUIApi {
    @Composable
    override fun getUpdateCardState(): State<UpdateCardState> {
        val updaterViewModel = viewModel<UpdaterViewModel>()
        return updaterViewModel.getUpdateCardState().collectAsState()
    }
}
