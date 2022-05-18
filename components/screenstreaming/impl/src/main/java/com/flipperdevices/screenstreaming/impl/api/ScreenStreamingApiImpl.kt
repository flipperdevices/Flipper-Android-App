package com.flipperdevices.screenstreaming.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.api.ScreenStreamingApi
import com.flipperdevices.screenstreaming.impl.composable.ComposableStreamingScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ScreenStreamingApiImpl @Inject constructor() : ScreenStreamingApi {
    @Composable
    override fun ProvideScreen() {
        ComposableStreamingScreen()
    }
}
