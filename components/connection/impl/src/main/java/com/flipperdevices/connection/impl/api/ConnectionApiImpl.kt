package com.flipperdevices.connection.impl.api

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.connection.impl.viewmodel.ConnectionStatusViewModel
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@ContributesBinding(AppGraph::class)
class ConnectionApiImpl @Inject constructor() : ConnectionApi {
    @Composable
    override fun getConnectionTabState(): StateFlow<TabState> {
        val connectionStatusViewModel: ConnectionStatusViewModel = viewModel()
        return connectionStatusViewModel.getStatusState()
    }
}
