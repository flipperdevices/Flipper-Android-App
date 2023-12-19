package com.flipperdevices.info.impl.api

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
import com.flipperdevices.info.api.screen.DeviceScreenDecomposeComponent
import com.flipperdevices.info.impl.model.DeviceScreenNavigationConfig
import com.flipperdevices.settings.api.SettingsDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DeviceScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val settingsFactory: SettingsDecomposeComponent.Factory,
    private val updateFactory: UpdateScreenDecomposeComponent.Factory,
    private val fullInfoDecomposeComponentFactory: FullInfoDecomposeComponent.Factory
) : DeviceScreenDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<DeviceScreenNavigationConfig>()

    val stack: Value<ChildStack<DeviceScreenNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = DeviceScreenNavigationConfig.serializer(),
        initialConfiguration = DeviceScreenNavigationConfig.Update(),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: DeviceScreenNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is DeviceScreenNavigationConfig.Update -> updateFactory(
            componentContext,
            config.deeplink,
            navigation
        )

        DeviceScreenNavigationConfig.FullInfo -> fullInfoDecomposeComponentFactory(
            componentContext = componentContext,
            onBack = navigation::pop
        )

        DeviceScreenNavigationConfig.Options -> settingsFactory(componentContext)
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

    @AssistedFactory
    @ContributesBinding(AppGraph::class, DeviceScreenDecomposeComponent.Factory::class)
    interface Factory : DeviceScreenDecomposeComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
        ): DeviceScreenDecomposeComponentImpl
    }
}
