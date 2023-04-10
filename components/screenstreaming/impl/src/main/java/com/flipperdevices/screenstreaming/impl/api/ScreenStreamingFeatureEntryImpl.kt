package com.flipperdevices.screenstreaming.impl.api

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.OnLifecycleEvent
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import com.flipperdevices.screenstreaming.impl.composable.ComposableStreamingScreen
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenshotViewModel
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import tangle.viewmodel.compose.tangleViewModel
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ScreenStreamingFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class ScreenStreamingFeatureEntryImpl @Inject constructor() : ScreenStreamingFeatureEntry {
    private fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                val screenStreamingViewModel: ScreenStreamingViewModel = tangleViewModel()
                val screenshotViewModel: ScreenshotViewModel = viewModel()

                OnLifecycleEvent {
                    when (it) {
                        Lifecycle.Event.ON_RESUME -> screenStreamingViewModel.enableStreaming()
                        Lifecycle.Event.ON_PAUSE -> screenStreamingViewModel.disableStreaming()
                        else -> {}
                    }
                }

                ComposableStreamingScreen(
                    screenStreamingViewModel = screenStreamingViewModel,
                    screenshotViewModel = screenshotViewModel,
                    onBack = navController::popBackStack
                )
            }
        }
    }
}
