package com.flipperdevices.remotecontrols.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.RemoteControlsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.api.model.RemoteControlsNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, RemoteControlsScreenDecomposeComponent.Factory::class)
class RemoteControlsScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val deeplink: Deeplink.RootLevel.RemoteControl?,
    private val categoriesScreenDecomposeComponentFactory: CategoriesScreenDecomposeComponent.Factory,
    private val brandsScreenDecomposeComponentFactory: BrandsScreenDecomposeComponent.Factory,
    private val setupScreenDecomposeComponentFactory: SetupScreenDecomposeComponent.Factory,
    private val gridScreenDecomposeComponentFactory: GridScreenDecomposeComponent.Factory
) : RemoteControlsScreenDecomposeComponent<RemoteControlsNavigationConfig>(),
    ComponentContext by componentContext {

    override val stack = childStack(
        source = navigation,
        serializer = RemoteControlsNavigationConfig.serializer(),
        initialStack = {
            when (deeplink) {
                is Deeplink.RootLevel.RemoteControl.Path -> listOf(
                    RemoteControlsNavigationConfig.Grid.Path(deeplink.flipperKeyPath)
                )

                null -> listOf(RemoteControlsNavigationConfig.SelectCategory)
            }
        },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun RemoteControlsNavigationConfig.Grid.toGridParam() = when (this) {
        is RemoteControlsNavigationConfig.Grid.Path -> {
            GridScreenDecomposeComponent.Param.Path(
                flipperKeyPath = this.flipperKeyPath,
            )
        }

        is RemoteControlsNavigationConfig.Grid.Id -> {
            GridScreenDecomposeComponent.Param.Id(
                irFileId = this.ifrFileId,
            )
        }
    }

    private fun child(
        config: RemoteControlsNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is RemoteControlsNavigationConfig.SelectCategory -> {
            categoriesScreenDecomposeComponentFactory(
                componentContext = componentContext,
                onBackClick = onBack::invoke,
                onCategoryClick = { deviceCategoryId ->
                    val configuration = RemoteControlsNavigationConfig.Brands(deviceCategoryId)
                    navigation.pushToFront(configuration)
                }
            )
        }

        is RemoteControlsNavigationConfig.Brands -> {
            brandsScreenDecomposeComponentFactory(
                componentContext = componentContext,
                onBackClick = navigation::pop,
                categoryId = config.categoryId,
                onBrandClick = { brandId ->
                    val configuration = RemoteControlsNavigationConfig.Setup(
                        categoryId = config.categoryId,
                        brandId = brandId
                    )
                    navigation.pushToFront(configuration)
                }
            )
        }

        is RemoteControlsNavigationConfig.Setup -> {
            setupScreenDecomposeComponentFactory(
                componentContext = componentContext,
                param = SetupScreenDecomposeComponent.Param(
                    brandId = config.brandId,
                    categoryId = config.categoryId
                ),
                onBack = navigation::pop,
                onIfrFileFound = {
                    val configuration = RemoteControlsNavigationConfig.Grid.Id(ifrFileId = it)
                    navigation.replaceCurrent(configuration)
                }
            )
        }

        is RemoteControlsNavigationConfig.Grid -> {
            gridScreenDecomposeComponentFactory(
                componentContext = componentContext,
                param = config.toGridParam(),
                onPopClick = navigation::pop
            )
        }
    }
}
