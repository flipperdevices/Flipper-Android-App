package com.flipperdevices.firstpair.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.api.FirstPairDecomposeComponent
import com.flipperdevices.firstpair.impl.model.FirstPairNavigationConfig
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FirstPairDecomposeComponent.Factory::class)
@Suppress("LongParameterList")
class FirstPairDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val invalidate: () -> Unit,
    private val firstPairStorage: FirstPairStorage,
    private val tosScreenFactory: TOSScreenDecomposeComponent.Factory,
    private val helpScreenFactory: HelpScreenDecomposeComponent.Factory,
    private val deviceScreenFactory: DeviceScreenDecomposeComponent.Factory
) : FirstPairDecomposeComponent<FirstPairNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<FirstPairNavigationConfig, DecomposeComponent>> = childStack(
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
            onHelpClick = { navigation.pushToFront(FirstPairNavigationConfig.HelpScreen) },
            onBack = {
                navigation.popOr(onBack::invoke)
            }
        )

        FirstPairNavigationConfig.HelpScreen -> helpScreenFactory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        FirstPairNavigationConfig.TOSScreen -> tosScreenFactory(
            componentContext = componentContext,
            onApply = {
                if (firstPairStorage.isDeviceSelected()) {
                    invalidate()
                } else {
                    navigation.pushToFront(FirstPairNavigationConfig.DeviceScreen)
                }
            }
        )
    }
}
