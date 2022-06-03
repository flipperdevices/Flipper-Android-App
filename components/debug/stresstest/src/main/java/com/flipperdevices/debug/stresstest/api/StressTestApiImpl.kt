package com.flipperdevices.debug.stresstest.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.api.StressTestApi
import com.flipperdevices.debug.stresstest.composable.ComposableStressTestScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class StressTestApiImpl @Inject constructor() : StressTestApi {
    @Composable
    override fun StressTestScreen() {
        ComposableStressTestScreen()
    }
}
