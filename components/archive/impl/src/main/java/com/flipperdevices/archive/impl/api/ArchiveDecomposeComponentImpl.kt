package com.flipperdevices.archive.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.value.Value
import com.flipperdevices.archive.api.ArchiveDecomposeComponent
import com.flipperdevices.archive.api.CategoryDecomposeComponent
import com.flipperdevices.archive.api.SearchDecomposeComponent
import com.flipperdevices.archive.impl.model.ArchiveNavigationConfig
import com.flipperdevices.archive.impl.model.toArchiveNavigationStack
import com.flipperdevices.bottombar.handlers.ResetTabDecomposeHandler
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.findComponentByConfig
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.ui.decompose.popToRoot
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, ArchiveDecomposeComponent.Factory::class)
class ArchiveDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar.ArchiveTab?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val openCategoryFactory: CategoryDecomposeComponent.Factory,
    private val searchFactory: SearchDecomposeComponent.Factory,
    private val archiveScreenFactory: ArchiveScreenDecomposeComponentImpl.Factory,
) : ArchiveDecomposeComponent<ArchiveNavigationConfig>(),
    ComponentContext by componentContext,
    ResetTabDecomposeHandler {

    override val stack: Value<ChildStack<ArchiveNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = ArchiveNavigationConfig.serializer(),
        initialStack = {
            deeplink.toArchiveNavigationStack()
        },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: ArchiveNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        ArchiveNavigationConfig.ArchiveObject -> archiveScreenFactory(
            componentContext = componentContext,
            navigation = navigation,
        )

        is ArchiveNavigationConfig.OpenCategory -> openCategoryFactory(
            componentContext = componentContext,
            categoryType = config.categoryType,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        ArchiveNavigationConfig.OpenSearch -> searchFactory(
            componentContext = componentContext,
            onItemSelected = null,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }

    override fun handleDeeplink(deeplink: Deeplink.BottomBar.ArchiveTab) {
        navigation.navigate { deeplink.toArchiveNavigationStack() }
    }

    override fun onResetTab() {
        navigation.popToRoot()
        val instance = stack.findComponentByConfig(ArchiveNavigationConfig.ArchiveObject::class)
        if (instance is ResetTabDecomposeHandler) {
            instance.onResetTab()
        }
    }
}
