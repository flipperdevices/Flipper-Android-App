package com.flipperdevices.toolstab.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bottombar.handlers.ResetTabDecomposeHandler
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.nfc.mfkey32.api.MfKey32DecomposeComponent
import com.flipperdevices.remotecontrols.api.RemoteControlsScreenDecomposeComponent
import com.flipperdevices.toolstab.api.ToolsDecomposeComponent
import com.flipperdevices.toolstab.impl.model.ToolsNavigationConfig
import com.flipperdevices.toolstab.impl.model.toConfigStack
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.ui.decompose.popToRoot
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, ToolsDecomposeComponent.Factory::class)
class ToolsDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar.ToolsTab?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val hubMainFactory: ToolsMainScreenDecomposeComponentImpl.Factory,
    private val mfKey32Factory: MfKey32DecomposeComponent.Factory,
    private val remoteControlsComponentFactory: RemoteControlsScreenDecomposeComponent.Factory,
) : ToolsDecomposeComponent<ToolsNavigationConfig>(),
    ComponentContext by componentContext,
    ResetTabDecomposeHandler {
    override val stack: Value<ChildStack<ToolsNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = ToolsNavigationConfig.serializer(),
        initialStack = { deeplink.toConfigStack() },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: ToolsNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        ToolsNavigationConfig.Main -> hubMainFactory(
            componentContext = componentContext,
            navigation = navigation
        )

        ToolsNavigationConfig.MfKey32 -> mfKey32Factory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        ToolsNavigationConfig.RemoteControls -> remoteControlsComponentFactory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }

    override fun handleDeeplink(deeplink: Deeplink.BottomBar.ToolsTab) {
        navigation.navigate { deeplink.toConfigStack() }
    }

    override fun onResetTab() {
        navigation.popToRoot()
    }
}
