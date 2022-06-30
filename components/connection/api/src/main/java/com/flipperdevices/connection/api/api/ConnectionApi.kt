package com.flipperdevices.connection.api.api

import androidx.compose.runtime.Composable
import com.flipperdevices.connection.api.model.ConnectionStatusState
import kotlinx.coroutines.flow.StateFlow

interface ConnectionApi {
    @Composable
    fun getConnectionTabState(): StateFlow<ConnectionStatusState>
}
