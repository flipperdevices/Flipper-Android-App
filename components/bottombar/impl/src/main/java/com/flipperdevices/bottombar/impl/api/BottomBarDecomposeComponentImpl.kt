package com.flipperdevices.bottombar.impl.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.archive.api.ArchiveDecomposeComponent
import com.flipperdevices.bottombar.api.BottomBarDecomposeComponent
import com.flipperdevices.bottombar.handlers.ResetTabDecomposeHandler
import com.flipperdevices.bottombar.impl.composable.ComposableMainScreen
import com.flipperdevices.bottombar.impl.model.BottomBarTabConfig
import com.flipperdevices.bottombar.impl.model.BottomBarTabEnum
import com.flipperdevices.bottombar.impl.model.toBottomBarTabEnum
import com.flipperdevices.bottombar.impl.model.toConfig
import com.flipperdevices.bottombar.impl.viewmodel.BottomBarViewModel
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationViewModel
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactoryWithoutRemember
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.hub.api.HubDecomposeComponent
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.flipperdevices.info.api.screen.DeviceScreenDecomposeComponent
import com.flipperdevices.notification.api.FlipperAppNotificationDialogApi
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.findComponentByConfig
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.unhandledexception.api.UnhandledExceptionRenderApi
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import javax.inject.Provider

@Suppress("LongParameterList")
class BottomBarDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val settingsDataStore: DataStore<Settings>,
    private val archiveScreenFactory: ArchiveDecomposeComponent.Factory,
    private val deviceScreenFactory: DeviceScreenDecomposeComponent.Factory,
    private val hubScreenFactory: HubDecomposeComponent.Factory,
    private val connectionApi: ConnectionApi,
    private val notificationRenderer: InAppNotificationRenderer,
    private val unhandledExceptionRendererApi: UnhandledExceptionRenderApi,
    private val appNotificationApi: FlipperAppNotificationDialogApi,
    private val bottomBarViewModelProvider: Provider<BottomBarViewModel>,
    private val inAppNotificationViewModelFactory: InAppNotificationViewModel.Factory
) : BottomBarDecomposeComponent<BottomBarTabConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<BottomBarTabConfig, DecomposeComponent>> =
        childStack(
            source = navigation,
            serializer = BottomBarTabConfig.serializer(),
            initialConfiguration = BottomBarTabConfig.getInitialConfig(settingsDataStore, deeplink),
            childFactory = ::child,
        )

    private val backCallback = BackCallback {
        navigation.popOr(onBack::invoke)
    }

    init {
        backHandler.register(backCallback)
    }

    private val notificationViewModel = viewModelWithFactoryWithoutRemember(key = this) {
        inAppNotificationViewModelFactory(this)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() = Box {
        val childStack by stack.subscribeAsState()
        val connectionTabState = connectionApi.getConnectionTabState(
            componentContext = this@BottomBarDecomposeComponentImpl
        )

        val bottomBarViewModel: BottomBarViewModel = viewModelWithFactory(key = null) {
            bottomBarViewModelProvider.get()
        }
        val hubHasNotification by bottomBarViewModel.hasNotificationHubState().collectAsState()

        ComposableMainScreen(
            notificationViewModel = notificationViewModel,
            notificationRenderer = notificationRenderer,
            childStack = childStack,
            connectionTabState = connectionTabState,
            onTabClick = ::goToTab,
            modifier = Modifier.fillMaxSize(),
            hubHasNotification = hubHasNotification
        )

        connectionApi.CheckAndShowUnsupportedDialog(
            componentContext = this@BottomBarDecomposeComponentImpl
        )
        appNotificationApi.NotificationDialog(
            componentContext = this@BottomBarDecomposeComponentImpl
        )
        unhandledExceptionRendererApi.ComposableUnhandledExceptionRender(Modifier)
    }

    private fun child(
        config: BottomBarTabConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is BottomBarTabConfig.Archive -> archiveScreenFactory(
            componentContext = componentContext,
            deeplink = config.deeplink,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is BottomBarTabConfig.Device -> deviceScreenFactory(
            componentContext = componentContext,
            deeplink = config.deeplink,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is BottomBarTabConfig.Hub -> hubScreenFactory(
            componentContext = componentContext,
            deeplink = config.deeplink,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }

    private fun goToTab(bottomBarTabEnum: BottomBarTabEnum, force: Boolean) {
        runBlocking {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setSelectedTab(bottomBarTabEnum.protobufRepresentation)
                    .build()
            }
        }

        val existedPair = stack.value.items.find { it.configuration.enum == bottomBarTabEnum }
        if (existedPair == null) {
            navigation.bringToFront(bottomBarTabEnum.toConfig())
            return
        }
        navigation.bringToFront(existedPair.configuration)

        if (force) {
            val instance = existedPair.instance
            if (instance is ResetTabDecomposeHandler) {
                instance.onResetTab()
            }
        }
    }

    override fun handleDeeplink(deeplink: Deeplink.BottomBar) {
        when (deeplink) {
            is Deeplink.BottomBar.ArchiveTab -> {
                val instance = stack.findComponentByConfig(BottomBarTabConfig.Archive::class)
                if (instance == null || instance !is ArchiveDecomposeComponent<*>) {
                    navigation.bringToFront(BottomBarTabConfig.Archive(deeplink))
                } else {
                    instance.handleDeeplink(deeplink)
                }
            }

            is Deeplink.BottomBar.DeviceTab -> {
                val instance = stack.findComponentByConfig(BottomBarTabConfig.Hub::class)
                if (instance == null || instance !is DeviceScreenDecomposeComponent<*>) {
                    navigation.bringToFront(BottomBarTabConfig.Device(deeplink))
                } else {
                    instance.handleDeeplink(deeplink)
                }
            }

            is Deeplink.BottomBar.HubTab -> {
                val instance = stack.findComponentByConfig(BottomBarTabConfig.Hub::class)
                if (instance == null || instance !is HubDecomposeComponent<*>) {
                    navigation.bringToFront(BottomBarTabConfig.Hub(deeplink))
                } else {
                    instance.handleDeeplink(deeplink)
                }
            }

            is Deeplink.BottomBar.OpenTab -> goToTab(
                deeplink.bottomTab.toBottomBarTabEnum(),
                force = true
            )
        }
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, BottomBarDecomposeComponent.Factory::class)
    fun interface Factory : BottomBarDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            deeplink: Deeplink.BottomBar?
        ): BottomBarDecomposeComponentImpl
    }
}
