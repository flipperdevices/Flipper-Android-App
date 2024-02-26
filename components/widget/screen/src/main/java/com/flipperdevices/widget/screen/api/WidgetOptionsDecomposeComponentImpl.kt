package com.flipperdevices.widget.screen.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import com.flipperdevices.widget.screen.compose.WidgetOptionsComposable
import com.flipperdevices.widget.screen.model.WidgetNavigationConfig
import com.flipperdevices.widget.screen.viewmodel.WidgetSelectViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class WidgetOptionsDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val widgetId: Int,
    @Assisted private val navigation: StackNavigation<WidgetNavigationConfig>,
    private val archiveApi: ArchiveApi,
    private val widgetSelectViewModelFactory: WidgetSelectViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val widgetSelectViewModel = viewModelWithFactory(key = widgetId.toString()) {
            widgetSelectViewModelFactory(widgetId)
        }
        WidgetOptionsComposable(
            archiveApi = archiveApi,
            widgetSelectViewModel = widgetSelectViewModel,
            onOpenSearchScreen = {
                navigation.pushToFront(
                    WidgetNavigationConfig.SearchScreen {
                        widgetSelectViewModel.onSelectKey(it)
                    }
                )
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            widgetId: Int,
            navigation: StackNavigation<WidgetNavigationConfig>
        ): WidgetOptionsDecomposeComponentImpl
    }
}
