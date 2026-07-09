package com.flipperdevices.faphub.screenshotspreview.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.screenshotspreview.api.ScreenshotsPreviewDecomposeComponent
import com.flipperdevices.faphub.screenshotspreview.api.model.ScreenshotsPreviewParam
import com.flipperdevices.faphub.screenshotspreview.impl.composable.ComposableFullScreenshotScreen
import com.flipperdevices.faphub.screenshotspreview.impl.viewmodel.ImageSelectViewModel
import com.flipperdevices.faphub.screenshotspreview.impl.viewmodel.UrlImageShareViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.kulikov.metro.assisted.ContributesAssistedFactory
import dev.zacsweers.metro.Provider

@ContributesAssistedFactory(AppGraph::class, ScreenshotsPreviewDecomposeComponent.Factory::class)
class FullScreenScreenshotDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: ScreenshotsPreviewParam,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val urlImageShareViewModelProvider: Provider<UrlImageShareViewModel>,
    private val imageSelectViewModelProvider: Provider<ImageSelectViewModel>,
) : ScreenshotsPreviewDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val urlImageShareViewModel = viewModelWithFactory(null) {
            urlImageShareViewModelProvider.invoke()
        }
        val imageSelectViewModel = viewModelWithFactory(null) {
            imageSelectViewModelProvider.invoke()
        }

        ComposableFullScreenshotScreen(
            imageSelectViewModel = imageSelectViewModel,
            urlImageShareViewModel = urlImageShareViewModel,
            onBack = onBack::invoke,
            title = param.title,
            selected = param.selected,
            screenshots = param.screenshotsUrls
        )
    }
}
