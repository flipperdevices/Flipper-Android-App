package com.flipperdevices.settings.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.debug.api.StressTestDecomposeComponent
import com.flipperdevices.filemanager.api.navigation.FileManagerDecomposeComponent
import com.flipperdevices.settings.api.SettingsDecomposeComponent
import com.flipperdevices.settings.impl.model.SettingsNavigationConfig
import com.flipperdevices.shake2report.api.Shake2ReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class SettingsDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val fileManagerComponentFactory: FileManagerDecomposeComponent.Factory,
    private val shake2ReportComponentFactory: Shake2ReportDecomposeComponent.Factory,
    private val mainComponentFactory: MainScreenDecomposeComponent.Factory,
    private val stressTestDecomposeComponentProvider: Provider<StressTestDecomposeComponent>
) : SettingsDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<SettingsNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = SettingsNavigationConfig.serializer(),
        initialConfiguration = SettingsNavigationConfig.Main,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: SettingsNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        SettingsNavigationConfig.FileManager -> fileManagerComponentFactory(componentContext)
        SettingsNavigationConfig.Main -> mainComponentFactory(componentContext, navigation)
        SettingsNavigationConfig.Shake2Report -> shake2ReportComponentFactory(onBack = navigation::pop)
        SettingsNavigationConfig.StressTest -> stressTestDecomposeComponentProvider.get()
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, SettingsDecomposeComponent.Factory::class)
    interface Factory : SettingsDecomposeComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
        ): SettingsDecomposeComponentImpl
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val childStack by stack.subscribeAsState()

        Children(
            stack = childStack,
        ) {
            it.instance.Render()
        }
    }
}
