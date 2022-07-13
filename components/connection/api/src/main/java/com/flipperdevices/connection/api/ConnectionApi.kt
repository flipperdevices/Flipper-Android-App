package com.flipperdevices.connection.api

import androidx.compose.runtime.Composable
import com.flipperdevices.bottombar.model.TabState

interface ConnectionApi {
    @Composable
    fun getConnectionTabState(): TabState
}
