package com.flipperdevices.hub.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
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
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.ui.decompose.popToRoot
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, HubDecomposeComponent.Factory::class)
class HubDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar.HubTab?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fapHubFactory: FapHubDecomposeComponent.Factory,
    private val hubMainFactory: HubMainScreenDecomposeComponentImpl.Factory,
    private val nfcAttackFactory: NFCAttackDecomposeComponent.Factory
) : HubDecomposeComponent<HubNavigationConfig>(),
    ComponentContext by componentContext,
    ResetTabDecomposeHandler {
    override val stack: Value<ChildStack<HubNavigationConfig, DecomposeComponent>> = childStack(
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
            onBack = { navigation.popOr(onBack::invoke) }
        )

        HubNavigationConfig.Main -> hubMainFactory(
            componentContext = componentContext,
            navigation = navigation
        )

        is HubNavigationConfig.NfcAttack -> nfcAttackFactory(
            componentContext = componentContext,
            deeplink = config.deeplink,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }

    override fun handleDeeplink(deeplink: Deeplink.BottomBar.HubTab) {
        navigation.navigate { deeplink.toConfigStack() }
    }

    override fun onResetTab() {
        navigation.popToRoot()
    }
}
