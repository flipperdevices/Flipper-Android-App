package com.flipperdevices.info.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bottombar.handlers.ResetTabDecomposeHandler
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.info.api.screen.DeviceScreenDecomposeComponent
import com.flipperdevices.info.impl.model.DeviceScreenNavigationConfig
import com.flipperdevices.settings.api.SettingsDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.findComponentByConfig
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.ui.decompose.popToRoot
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, DeviceScreenDecomposeComponent.Factory::class)
class DeviceScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar.DeviceTab?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val settingsFactory: SettingsDecomposeComponent.Factory,
    private val updateFactory: UpdateScreenDecomposeComponent.Factory,
    private val fullInfoDecomposeComponentFactory: FullInfoDecomposeComponent.Factory
) : DeviceScreenDecomposeComponent<DeviceScreenNavigationConfig>(),
    ComponentContext by componentContext,
    ResetTabDecomposeHandler {

    override val stack: Value<ChildStack<DeviceScreenNavigationConfig, DecomposeComponent>> =
        childStack(
            source = navigation,
            serializer = DeviceScreenNavigationConfig.serializer(),
            initialConfiguration = DeviceScreenNavigationConfig.Update(
                deeplink as? Deeplink.BottomBar.DeviceTab.WebUpdate
            ),
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
            onBack = { navigation.popOr(onBack::invoke) }
        )

        DeviceScreenNavigationConfig.Options -> settingsFactory(
            componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }

    override fun onResetTab() {
        navigation.popToRoot()
        val instance = stack.findComponentByConfig(DeviceScreenNavigationConfig.Update::class)
        if (instance is ResetTabDecomposeHandler) {
            instance.onResetTab()
        }
    }

    override fun handleDeeplink(deeplink: Deeplink.BottomBar.DeviceTab) {
        when (deeplink) {
            Deeplink.BottomBar.DeviceTab.OpenUpdate -> navigation.replaceAll(
                DeviceScreenNavigationConfig.Update(null)
            )

            is Deeplink.BottomBar.DeviceTab.WebUpdate -> navigation.replaceAll(
                DeviceScreenNavigationConfig.Update(deeplink)
            )
        }
    }
}
