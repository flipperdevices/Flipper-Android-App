package com.flipperdevices.connection.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.connection.impl.dialog.ComposableUnsupportedDialog
import com.flipperdevices.connection.impl.viewmodel.ConnectionStatusViewModel
import com.flipperdevices.connection.impl.viewmodel.ConnectionTabStateMapper
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ConnectionApiImpl @Inject constructor() : ConnectionApi {
    @Composable
    override fun getConnectionTabState(): TabState {
        val connectionStatusViewModel: ConnectionStatusViewModel = viewModel()
        val connectionStatusState by connectionStatusViewModel.getStatusState().collectAsState()
        return ConnectionTabStateMapper.getConnectionTabState(connectionStatusState)
    }

    @Composable
    override fun CheckAndShowUnsupportedDialog() {
        ComposableUnsupportedDialog()
    }
}
