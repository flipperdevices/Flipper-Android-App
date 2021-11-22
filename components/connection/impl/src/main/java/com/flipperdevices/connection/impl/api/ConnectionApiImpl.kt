package com.flipperdevices.connection.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.connection.impl.composable.ComposableConnection
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ConnectionApiImpl @Inject constructor() : ConnectionApi {
    @Composable
    override fun ConnectionTab() {
        ComposableConnection()
    }
}
