package com.flipperdevices.screenstreaming.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactoryWithoutRemember
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.screenstreaming.impl.composable.ComposableStreamingScreen
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenshotViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, ScreenStreamingDecomposeComponent.Factory::class)
class ScreenStreamingDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val screenStreamingViewModelFactory: ScreenStreamingViewModel.Factory,
    private val screenshotViewModelProvider: Provider<ScreenshotViewModel>
) : ScreenStreamingDecomposeComponent(componentContext) {
    private val screenStreamingViewModel = viewModelWithFactoryWithoutRemember(key = this) {
        screenStreamingViewModelFactory(this@ScreenStreamingDecomposeComponentImpl)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val screenshotViewModel = viewModelWithFactory(null) {
            screenshotViewModelProvider.get()
        }

        ComposableStreamingScreen(
            screenStreamingViewModel = screenStreamingViewModel,
            screenshotViewModel = screenshotViewModel,
            onBack = onBack::invoke,
        )
    }
}
