package com.flipperdevices.bottombar.impl.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.archive.api.ArchiveDecomposeComponent
import com.flipperdevices.bottombar.impl.composable.ComposableInAppNotification
import com.flipperdevices.bottombar.impl.composable.ComposableMainScreen
import com.flipperdevices.bottombar.impl.model.BottomBarNavigationConfig
import com.flipperdevices.bottombar.impl.model.BottomBarTabEnum
import com.flipperdevices.bottombar.impl.model.toConfig
import com.flipperdevices.bottombar.impl.viewmodel.BottomBarViewModel
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationViewModel
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ktx.android.OnLifecycleEvent
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.hub.api.HubDecomposeComponent
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.flipperdevices.info.api.screen.DeviceScreenDecomposeComponent
import com.flipperdevices.notification.api.FlipperAppNotificationDialogApi
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.unhandledexception.api.UnhandledExceptionRenderApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import javax.inject.Provider

@Suppress("LongParameterList")
class BottomBarDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted onBack: DecomposeOnBackParameter,
    private val settingsDataStore: DataStore<Settings>,
    private val archiveScreenFactory: ArchiveDecomposeComponent.Factory,
    private val deviceScreenFactory: DeviceScreenDecomposeComponent.Factory,
    private val hubScreenFactory: HubDecomposeComponent.Factory,
    private val connectionApi: ConnectionApi,
    private val notificationRenderer: InAppNotificationRenderer,
    private val unhandledExceptionRendererApi: UnhandledExceptionRenderApi,
    private val appNotificationApi: FlipperAppNotificationDialogApi,
    private val bottomBarViewModelProvider: Provider<BottomBarViewModel>,
    private val inAppNotificationViewModelProvider: Provider<InAppNotificationViewModel>
) : DecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<BottomBarNavigationConfig>()

    private val stack: Value<ChildStack<BottomBarNavigationConfig, DecomposeComponent>> =
        childStack(
            source = navigation,
            serializer = BottomBarNavigationConfig.serializer(),
            initialConfiguration = BottomBarNavigationConfig.getInitialSync(settingsDataStore),
            childFactory = ::child,
        )

    private val backCallback = BackCallback {
        navigation.pop(onComplete = { isComplete ->
            if (!isComplete) {
                onBack()
            }
        })
    }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() = Box {
        val childStack by stack.subscribeAsState()
        val connectionTabState = connectionApi.getConnectionTabState()

        val bottomBarViewModel: BottomBarViewModel = viewModelWithFactory(key = null) {
            bottomBarViewModelProvider.get()
        }
        val hubHasNotification by bottomBarViewModel.hasNotificationHubState().collectAsState()

        ComposableMainScreen(
            childStack = childStack,
            connectionTabState = connectionTabState,
            onTabClick = ::goToTab,
            modifier = Modifier.fillMaxSize(),
            hubHasNotification = hubHasNotification
        )

        val notificationViewModel: InAppNotificationViewModel = viewModelWithFactory(key = null) {
            inAppNotificationViewModelProvider.get()
        }
        val notificationState by notificationViewModel.state().collectAsState()

        ComposableInAppNotification(
            modifier = Modifier.align(Alignment.BottomCenter),
            notificationRenderer = notificationRenderer,
            notificationState = notificationState,
            onNotificationHidden = notificationViewModel::onNotificationHidden
        )
        OnLifecycleEvent(onEvent = notificationViewModel::onLifecycleEvent)

        connectionApi.CheckAndShowUnsupportedDialog()
        appNotificationApi.NotificationDialog()
        unhandledExceptionRendererApi.ComposableUnhandledExceptionRender(Modifier)
    }

    private fun child(
        config: BottomBarNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is BottomBarNavigationConfig.Archive -> archiveScreenFactory(
            componentContext = componentContext
        )

        is BottomBarNavigationConfig.Device -> deviceScreenFactory(
            componentContext = componentContext
        )

        is BottomBarNavigationConfig.Hub -> hubScreenFactory(
            componentContext = componentContext
        )
    }

    private fun goToTab(bottomBarTabEnum: BottomBarTabEnum, force: Boolean) {
        navigation.navigate(
            transformer = { stack ->
                var newConfiguration = stack.findLast { it.enum == bottomBarTabEnum }
                    ?: bottomBarTabEnum.toConfig()
                if (force) {
                    newConfiguration = bottomBarTabEnum.toConfig(newConfiguration.uniqueId + 1)
                }

                stack.filterNot { it.enum == bottomBarTabEnum } + newConfiguration
            },
            onComplete = { _, _ -> },
        )

        runBlocking {
            settingsDataStore.updateData {
                it.toBuilder()
                    .setSelectedTab(bottomBarTabEnum.toConfig().protobufRepresentation)
                    .build()
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
        ): BottomBarDecomposeComponentImpl
    }
}
