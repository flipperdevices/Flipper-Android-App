package com.flipperdevices.remotecontrols.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
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
    private val categoriesScreenDecomposeComponentFactory: CategoriesScreenDecomposeComponent.Factory,
    private val brandsScreenDecomposeComponentFactory: BrandsScreenDecomposeComponent.Factory,
    private val setupScreenDecomposeComponentFactory: SetupScreenDecomposeComponent.Factory,
) : RemoteControlsScreenDecomposeComponent<RemoteControlsNavigationConfig>(),
    ComponentContext by componentContext {

    override val stack = childStack(
        source = navigation,
        serializer = RemoteControlsNavigationConfig.serializer(),
        initialConfiguration = RemoteControlsNavigationConfig.SelectCategory,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: RemoteControlsNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is RemoteControlsNavigationConfig.SelectCategory -> {
            categoriesScreenDecomposeComponentFactory(
                componentContext = componentContext,
                onBackClick = onBack::invoke,
                onCategoryClick = { deviceCategoryId, categoryName ->
                    val configuration = RemoteControlsNavigationConfig.Brands(
                        deviceCategoryId,
                        categoryName
                    )
                    navigation.pushToFront(configuration)
                }
            )
        }

        is RemoteControlsNavigationConfig.Brands -> {
            brandsScreenDecomposeComponentFactory(
                componentContext = componentContext,
                onBackClick = navigation::pop,
                categoryId = config.categoryId,
                onBrandClick = { brandId, brandName ->
                    val configuration = RemoteControlsNavigationConfig.Setup(
                        categoryId = config.categoryId,
                        brandId = brandId,
                        categoryName = config.categoryName,
                        brandName = brandName
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
                    categoryId = config.categoryId,
                    brandName = config.brandName,
                    categoryName = config.categoryName
                ),
                onBack = navigation::pop,
                onIrFileReady = { navigation.pop() }
            )
        }
    }
}
