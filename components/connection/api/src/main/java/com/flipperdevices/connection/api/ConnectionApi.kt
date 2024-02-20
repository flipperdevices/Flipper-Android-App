package com.flipperdevices.connection.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bottombar.model.TabState

interface ConnectionApi {
    @Composable
    fun getConnectionTabState(componentContext: ComponentContext): TabState

    @Composable
    fun CheckAndShowUnsupportedDialog(componentContext: ComponentContext)
}
