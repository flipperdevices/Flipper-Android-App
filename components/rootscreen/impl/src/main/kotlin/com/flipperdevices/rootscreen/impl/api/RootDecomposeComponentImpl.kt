package com.flipperdevices.rootscreen.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.flipperdevices.bottombar.api.BottomBarDecomposeComponent
import com.flipperdevices.changelog.api.ChangelogScreenDecomposeComponent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.faphub.screenshotspreview.api.ScreenshotsPreviewDecomposeComponent
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.firstpair.api.FirstPairDecomposeComponent
import com.flipperdevices.keyscreen.api.KeyScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.ConfigureGridDecomposeComponent
import com.flipperdevices.remotecontrols.api.RemoteControlsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.rootscreen.api.RootDecomposeComponent
import com.flipperdevices.rootscreen.impl.deeplink.RootDeeplinkHandler
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.share.api.KeyReceiveDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterDecomposeComponent
import com.flipperdevices.widget.api.WidgetDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, RootDecomposeComponent.Factory::class)
@Suppress("LongParameterList")
class RootDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted initialDeeplink: Deeplink?,
    private val firstPairApi: FirstPairApi,
    private val firstPairFactory: FirstPairDecomposeComponent.Factory,
    private val bottomBarFactory: BottomBarDecomposeComponent.Factory,
    private val updaterFactory: UpdaterDecomposeComponent.Factory,
    private val updaterApi: UpdaterApi,
    private val screenStreamingFactory: ScreenStreamingDecomposeComponent.Factory,
    private val widgetScreenFactory: WidgetDecomposeComponent.Factory,
    private val receiveKeyFactory: KeyReceiveDecomposeComponent.Factory,
    private val keyScreenFactory: KeyScreenDecomposeComponent.Factory,
    private val screenshotsPreviewFactory: ScreenshotsPreviewDecomposeComponent.Factory,
    private val changelogScreenDecomposeFactory: ChangelogScreenDecomposeComponent.Factory,
    private val remoteControlsComponentFactory: RemoteControlsScreenDecomposeComponent.Factory,
    private val serverRemoteControlFactory: ConfigureGridDecomposeComponent.Factory
) : RootDecomposeComponent, ComponentContext by componentContext {
    private val scope = coroutineScope(FlipperDispatchers.workStealingDispatcher)
    private val navigation = StackNavigation<RootScreenConfig>()

    private val stack: Value<ChildStack<RootScreenConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = RootScreenConfig.serializer(),
        initialStack = { getInitialConfiguration(initialDeeplink) },
        handleBackButton = true,
        childFactory = ::child,
    )
    private val deeplinkHandler = RootDeeplinkHandler(navigation, stack, firstPairApi)

    @Suppress("LongMethod")
    private fun child(
        config: RootScreenConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is RootScreenConfig.BottomBar -> bottomBarFactory(
            componentContext = componentContext,
            onBack = this::internalOnBack,
            deeplink = config.bottomBarDeeplink
        )

        is RootScreenConfig.FirstPair -> firstPairFactory(
            componentContext = componentContext,
            onBack = this::internalOnBack,
            invalidate = {
                navigation.navigate { getInitialConfiguration(config.pendingDeeplink) }
            }
        )

        is RootScreenConfig.UpdateScreen -> updaterFactory(
            componentContext = componentContext,
            updateRequest = config.updateRequest,
            onBack = this::internalOnBack
        )

        RootScreenConfig.ScreenStreaming -> screenStreamingFactory(
            componentContext = componentContext,
            onBack = this::internalOnBack
        )

        is RootScreenConfig.WidgetOptions -> widgetScreenFactory(
            componentContext = componentContext,
            widgetId = config.widgetId,
            onBack = this::internalOnBack
        )

        is RootScreenConfig.SaveKey -> receiveKeyFactory(
            componentContext = componentContext,
            deeplink = config.saveKeyDeeplink,
            onBack = this::internalOnBack
        )

        is RootScreenConfig.OpenKey -> keyScreenFactory(
            componentContext = componentContext,
            keyPath = config.flipperKeyPath,
            onBack = this::internalOnBack
        )

        is RootScreenConfig.ScreenshotPreview -> screenshotsPreviewFactory(
            componentContext = componentContext,
            param = config.param,
            onBack = this::internalOnBack
        )

        is RootScreenConfig.Changelog -> changelogScreenDecomposeFactory(
            componentContext = componentContext,
            updateRequest = config.updateRequest,
            onBack = this::internalOnBack
        )

        is RootScreenConfig.RemoteControls -> remoteControlsComponentFactory(
            componentContext = componentContext,
            onBack = this::internalOnBack
        )

        is RootScreenConfig.ServerRemoteControl -> serverRemoteControlFactory(
            componentContext = componentContext,
            param = ServerRemoteControlParam(
                infraredFileId = config.infraredFileId,
                remoteName = config.remoteName
            ),
            onBack = this::internalOnBack,
        )
    }

    private fun getInitialConfiguration(deeplink: Deeplink?): List<RootScreenConfig> {
        if (firstPairApi.shouldWeOpenPairScreen()) {
            return listOf(RootScreenConfig.FirstPair(deeplink))
        }
        val stack = mutableListOf<RootScreenConfig>()

        stack.addAll(RootDeeplinkHandler.getConfigStackFromDeeplink(deeplink))

        if (updaterApi.isUpdateInProcess()) {
            stack.add(RootScreenConfig.UpdateScreen(null))
        }

        return stack
    }

    private fun internalOnBack() {
        navigation.popOr(onBack::invoke)
    }

    override fun push(config: RootScreenConfig) {
        navigation.pushToFront(config)
    }

    override fun handleDeeplink(deeplink: Deeplink) {
        scope.launch(Dispatchers.Main) {
            deeplinkHandler.handleDeeplink(deeplink)
            if (deeplink is Deeplink.BottomBar.ArchiveTab.ArchiveCategory.OpenKey) {
                navigation.pushToFront(RootScreenConfig.OpenKey(deeplink.keyPath))
            }
        }
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render(modifier: Modifier) {
        val childStack by stack.subscribeAsState()

        Children(
            modifier = modifier,
            stack = childStack,
        ) {
            it.instance.Render()
        }
    }
}
