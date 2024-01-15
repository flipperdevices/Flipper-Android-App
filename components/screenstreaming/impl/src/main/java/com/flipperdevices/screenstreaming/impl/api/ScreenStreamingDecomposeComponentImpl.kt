package com.flipperdevices.screenstreaming.impl.api

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.OnLifecycleEvent
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.screenstreaming.impl.composable.ComposableStreamingScreen
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenshotViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class ScreenStreamingDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val screenStreamingViewModelProvider: Provider<ScreenStreamingViewModel>,
    private val screenshotViewModelProvider: Provider<ScreenshotViewModel>
) : ScreenStreamingDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val screenStreamingViewModel = viewModelWithFactory(key = null) {
            screenStreamingViewModelProvider.get()
        }
        val screenshotViewModel = viewModelWithFactory(null) {
            screenshotViewModelProvider.get()
        }

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
            onBack = onBack::invoke,
        )
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, ScreenStreamingDecomposeComponent.Factory::class)
    interface Factory : ScreenStreamingDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): ScreenStreamingDecomposeComponentImpl
    }
}
