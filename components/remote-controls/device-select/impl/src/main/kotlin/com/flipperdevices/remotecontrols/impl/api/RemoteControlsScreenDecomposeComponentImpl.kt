package com.flipperdevices.remotecontrols.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.backend.di.ApiBackendModule
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.api.di.SelectDeviceRootModule
import com.flipperdevices.remotecontrols.api.RemoteControlsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import  com.flipperdevices.remotecontrols.impl.api.model.RemoteControlsNavigationConfig

@ContributesAssistedFactory(AppGraph::class, RemoteControlsScreenDecomposeComponent.Factory::class)
class RemoteControlsScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
) : RemoteControlsScreenDecomposeComponent<RemoteControlsNavigationConfig>(),
    ComponentContext by componentContext {

    private val module = SelectDeviceRootModule.Default(
        apiBackendModule = ApiBackendModule.Default()
    )

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
            module
                .createDeviceCategoriesModule()
                .categoriesScreenDecomposeComponentFactory
                .create(
                    componentContext = componentContext,
                    onBackClicked = { onBack.invoke() },
                    onCategoryClicked = { deviceCategoryId ->
                        val configuration = RemoteControlsNavigationConfig.Brands(deviceCategoryId)
                        navigation.push(configuration)
                    }
                )
        }

        is RemoteControlsNavigationConfig.Brands -> {
            module
                .createBrandsModule()
                .brandsScreenDecomposeComponentFactory
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
            module
                .createSetupModule()
                .setupScreenDecomposeComponentFactory
                .createSetupComponent(
                    componentContext = componentContext,
                    param = SetupScreenDecomposeComponent.Param(
                        brandId = config.brandId,
                        categoryId = config.categoryId
                    ),
                    onBack = navigation::pop,
                    onIfrFileFound = {
                        val configuration = RemoteControlsNavigationConfig.Grid(ifrFileId = it)
                        navigation.replaceAll(configuration)
                    }
                )
        }

        is RemoteControlsNavigationConfig.Grid -> {
            module
                .createGridModule()
                .gridComponentFactory
                .create(
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
