package com.flipperdevices.connection.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bottombar.model.TabState
import kotlinx.coroutines.flow.StateFlow

interface ConnectionApi {
    @Composable
    fun getConnectionTabState(): StateFlow<TabState>
}
