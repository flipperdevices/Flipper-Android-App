package com.flipperdevices.hub.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.main.api.FapHubDecomposeComponent
import com.flipperdevices.hub.api.HubDecomposeComponent
import com.flipperdevices.hub.impl.model.HubNavigationConfig
import com.flipperdevices.nfc.attack.api.NFCAttackDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HubDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val fapHubFactory: FapHubDecomposeComponent.Factory,
    private val hubMainFactory: HubMainScreenDecomposeComponentImpl.Factory,
    private val nfcAttackFactory: NFCAttackDecomposeComponent.Factory
) : HubDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<HubNavigationConfig>()

    val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = HubNavigationConfig.serializer(),
        initialConfiguration = HubNavigationConfig.Main,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: HubNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        HubNavigationConfig.FapHub -> fapHubFactory(componentContext = componentContext)
        HubNavigationConfig.Main -> hubMainFactory(
            componentContext = componentContext,
            navigation = navigation
        )

        HubNavigationConfig.NfcAttack -> nfcAttackFactory(
            componentContext = componentContext
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
    @ContributesBinding(AppGraph::class, HubDecomposeComponent.Factory::class)
    fun interface Factory : HubDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext
        ): HubDecomposeComponentImpl
    }
}
