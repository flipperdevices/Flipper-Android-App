package com.flipperdevices.connection.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.connection.impl.dialog.ComposableUnsupportedDialog
import com.flipperdevices.connection.impl.viewmodel.ConnectionStatusViewModel
import com.flipperdevices.connection.impl.viewmodel.ConnectionTabStateMapper
import com.flipperdevices.connection.impl.viewmodel.UnsupportedStateViewModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider

@ContributesBinding(AppGraph::class)
class ConnectionApiImpl @Inject constructor(
    private val connectionStatusVMProvider: Provider<ConnectionStatusViewModel>,
    private val unsupportedStateVMProvider: Provider<UnsupportedStateViewModel>
) : ConnectionApi {
    @Composable
    override fun getConnectionTabState(
        componentContext: ComponentContext
    ): TabState {
        val connectionStatusViewModel = componentContext.viewModelWithFactory(null) {
            connectionStatusVMProvider.invoke()
        }
        val connectionStatusState by connectionStatusViewModel.getStatusState().collectAsState()
        return ConnectionTabStateMapper.getConnectionTabState(connectionStatusState)
    }

    @Composable
    override fun CheckAndShowUnsupportedDialog(
        componentContext: ComponentContext
    ) {
        val viewModel = componentContext.viewModelWithFactory(null) {
            unsupportedStateVMProvider.invoke()
        }
        val supportedState by viewModel.getUnsupportedState().collectAsState()
        ComposableUnsupportedDialog(supportedState)
    }
}
