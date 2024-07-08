package com.flipperdevices.remotecontrols.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.core.di.AppGraph
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

@ContributesAssistedFactory(AppGraph::class, RemoteControlsScreenDecomposeComponent.Factory::class)
class RemoteControlsScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val categoriesScreenDecomposeComponentFactory: CategoriesScreenDecomposeComponent.Factory,
    private val brandsScreenDecomposeComponentFactory: BrandsScreenDecomposeComponent.Factory,
    private val setupScreenDecomposeComponentFactory: SetupScreenDecomposeComponent.Factory,
    private val gridScreenDecomposeComponentFactory: GridScreenDecomposeComponent.Factory
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
            categoriesScreenDecomposeComponentFactory
                .invoke(
                    componentContext = componentContext,
                    onBackClicked = onBack::invoke,
                    onCategoryClicked = { deviceCategoryId ->
                        val configuration = RemoteControlsNavigationConfig.Brands(deviceCategoryId)
                        navigation.push(configuration)
                    }
                )
        }

        is RemoteControlsNavigationConfig.Brands -> {
            brandsScreenDecomposeComponentFactory
                .createBrandsComponent(
                    componentContext = componentContext,
                    onBackClicked = navigation::pop,
                    categoryId = config.categoryId,
                    onBrandClicked = { brandId ->
                        val configuration = RemoteControlsNavigationConfig.Setup(
                            categoryId = config.categoryId,
                            brandId = brandId
                        )
                        navigation.push(configuration)
                    }
                )
        }

        is RemoteControlsNavigationConfig.Setup -> {
            setupScreenDecomposeComponentFactory
                .invoke(
                    componentContext = componentContext,
                    param = SetupScreenDecomposeComponent.Param(
                        brandId = config.brandId,
                        categoryId = config.categoryId
                    ),
                    onBack = navigation::pop,
                    onIfrFileFound = {
                        val configuration = RemoteControlsNavigationConfig.Grid(ifrFileId = it)
                        navigation.push(configuration)
                    }
                )
        }

        is RemoteControlsNavigationConfig.Grid -> {
            gridScreenDecomposeComponentFactory
                .invoke(
                    componentContext = componentContext,
                    param = GridScreenDecomposeComponent.Param(
                        ifrFileId = config.ifrFileId,
                        uiFileId = null,
                    ),
                    onPopClicked = onBack::invoke
                )
        }
    }
}
