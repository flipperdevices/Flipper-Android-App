package com.flipperdevices.firstpair.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.api.FirstPairDecomposeComponent
import com.flipperdevices.firstpair.impl.model.FirstPairNavigationConfig
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("LongParameterList")
class FirstPairDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val invalidate: () -> Unit,
    private val firstPairStorage: FirstPairStorage,
    private val tosScreenFactory: TOSScreenDecomposeComponent.Factory,
    private val helpScreenFactory: HelpScreenDecomposeComponent.Factory,
    private val deviceScreenFactory: DeviceScreenDecomposeComponent.Factory
) : FirstPairDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<FirstPairNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FirstPairNavigationConfig.serializer(),
        initialConfiguration = if (firstPairStorage.isTosPassed()) {
            FirstPairNavigationConfig.DeviceScreen
        } else {
            FirstPairNavigationConfig.TOSScreen
        },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FirstPairNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        FirstPairNavigationConfig.DeviceScreen -> deviceScreenFactory(
            componentContext = componentContext,
            onFinishConnect = invalidate,
            onHelpClick = { navigation.push(FirstPairNavigationConfig.HelpScreen) },
            onBack = {
                navigation.pop {
                    if (!it) {
                        onBack()
                    }
                }
            }
        )

        FirstPairNavigationConfig.HelpScreen -> helpScreenFactory(
            componentContext = componentContext,
            onBack = navigation::pop
        )

        FirstPairNavigationConfig.TOSScreen -> tosScreenFactory(
            componentContext = componentContext,
            onApply = {
                if (firstPairStorage.isDeviceSelected()) {
                    invalidate()
                } else {
                    navigation.push(FirstPairNavigationConfig.DeviceScreen)
                }
            }
        )
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
    @ContributesBinding(AppGraph::class, FirstPairDecomposeComponent.Factory::class)
    interface Factory : FirstPairDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            invalidate: () -> Unit
        ): FirstPairDecomposeComponentImpl
    }
}
