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
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bottombar.api.BottomBarDecomposeComponent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.firstpair.api.FirstPairDecomposeComponent
import com.flipperdevices.rootscreen.api.RootDecomposeComponent
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.api.UpdaterDecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("LongParameterList")
class RootDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val firstPairApi: FirstPairApi,
    private val firstPairFactory: FirstPairDecomposeComponent.Factory,
    private val bottomBarFactory: BottomBarDecomposeComponent.Factory,
    private val updaterFactory: UpdaterDecomposeComponent.Factory,
    private val updaterApi: UpdaterApi,
    private val screenStreamingFactory: ScreenStreamingDecomposeComponent.Factory
) : RootDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<RootScreenConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = RootScreenConfig.serializer(),
        initialStack = this::getInitialConfiguration,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: RootScreenConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        RootScreenConfig.BottomBar -> bottomBarFactory(
            componentContext = componentContext,
            onBack = this::internalOnBack
        )

        RootScreenConfig.FirstPair -> firstPairFactory(
            componentContext = componentContext,
            onBack = this::internalOnBack,
            invalidate = {
                @Suppress("SpreadOperator")
                navigation.replaceAll(*getInitialConfiguration().toTypedArray())
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
    }

    private fun getInitialConfiguration(): List<RootScreenConfig> {
        val rootConfig = if (firstPairApi.shouldWeOpenPairScreen()) {
            RootScreenConfig.FirstPair
        } else {
            RootScreenConfig.BottomBar
        }
        if (updaterApi.isUpdateInProcess()) {
            return listOf(rootConfig, RootScreenConfig.UpdateScreen(null))
        }

        return listOf(rootConfig)
    }

    private fun internalOnBack() {
        navigation.pop { isSuccess ->
            if (!isSuccess) {
                onBack()
            }
        }
    }

    override fun push(config: RootScreenConfig) {
        navigation.push(config)
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

    @AssistedFactory
    @ContributesBinding(AppGraph::class, RootDecomposeComponent.Factory::class)
    interface Factory : RootDecomposeComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): RootDecomposeComponentImpl
    }
}
