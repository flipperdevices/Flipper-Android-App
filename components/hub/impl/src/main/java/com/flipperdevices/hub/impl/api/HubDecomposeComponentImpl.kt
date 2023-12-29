package com.flipperdevices.hub.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bottombar.handlers.ResetTabDecomposeHandler
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.faphub.main.api.FapHubDecomposeComponent
import com.flipperdevices.hub.api.HubDecomposeComponent
import com.flipperdevices.hub.impl.model.HubNavigationConfig
import com.flipperdevices.hub.impl.model.toConfigStack
import com.flipperdevices.nfc.attack.api.NFCAttackDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.popToRoot
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HubDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar.HubTab?,
    private val fapHubFactory: FapHubDecomposeComponent.Factory,
    private val hubMainFactory: HubMainScreenDecomposeComponentImpl.Factory,
    private val nfcAttackFactory: NFCAttackDecomposeComponent.Factory
) : HubDecomposeComponent, ComponentContext by componentContext, ResetTabDecomposeHandler {
    private val navigation = StackNavigation<HubNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = HubNavigationConfig.serializer(),
        initialStack = { deeplink.toConfigStack() },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: HubNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is HubNavigationConfig.FapHub -> fapHubFactory(
            componentContext = componentContext,
            deeplink = config.deeplink,
            onBack = navigation::pop
        )

        HubNavigationConfig.Main -> hubMainFactory(
            componentContext = componentContext,
            navigation = navigation
        )

        is HubNavigationConfig.NfcAttack -> nfcAttackFactory(
            componentContext = componentContext,
            deeplink = config.deeplink,
            onBack = navigation::pop
        )
    }

    override fun handleDeeplink(deeplink: Deeplink.BottomBar.HubTab) {
        navigation.navigate { deeplink.toConfigStack() }
    }

    override fun onResetTab() {
        navigation.popToRoot()
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
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.HubTab?
        ): HubDecomposeComponentImpl
    }
}
