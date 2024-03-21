package com.flipperdevices.wearrootscreen.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.wearable.emulate.api.WearEmulateDecomposeComponent
import com.flipperdevices.wearable.sync.wear.api.KeysListDecomposeComponent
import com.flipperdevices.wearrootscreen.api.WearRootDecomposeComponent
import com.flipperdevices.wearrootscreen.model.WearRootConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, WearRootDecomposeComponent.Factory::class)
class WearRootDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBackParameter: DecomposeOnBackParameter,
    private val keysListFactory: KeysListDecomposeComponent.Factory,
    private val keyScreenFactory: WearEmulateDecomposeComponent.Factory
) : WearRootDecomposeComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<WearRootConfig>()

    override val stack: Value<ChildStack<WearRootConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = WearRootConfig.serializer(),
        initialConfiguration = WearRootConfig.KeysList,
        handleBackButton = false,
        childFactory = ::child,
    )

    private fun child(
        config: WearRootConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        WearRootConfig.KeysList -> keysListFactory(
            componentContext = componentContext,
            navigation = navigation
        )

        is WearRootConfig.OpenKey -> keyScreenFactory(
            componentContext = componentContext,
            flipperKeyPath = config.path,
            onBack = ::onBack
        )
    }

    override fun onBack() {
        navigation.popOr(onBackParameter::invoke)
    }
}
