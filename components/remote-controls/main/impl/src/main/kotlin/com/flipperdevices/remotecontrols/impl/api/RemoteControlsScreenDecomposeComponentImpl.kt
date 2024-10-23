package com.flipperdevices.remotecontrols.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.InfraredsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.RemoteControlsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.grid.remote.api.RemoteGridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.grid.remote.api.model.ServerRemoteControlParam
import com.flipperdevices.remotecontrols.impl.api.model.RemoteControlsNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, RemoteControlsScreenDecomposeComponent.Factory::class)
class RemoteControlsScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onDeeplink: (Deeplink.BottomBar) -> Unit,
    private val categoriesScreenDecomposeComponentFactory: CategoriesScreenDecomposeComponent.Factory,
    private val brandsScreenDecomposeComponentFactory: BrandsScreenDecomposeComponent.Factory,
    private val setupScreenDecomposeComponentFactory: SetupScreenDecomposeComponent.Factory,
    private val infraredsScreenDecomposeComponentFactory: InfraredsScreenDecomposeComponent.Factory,
    private val remoteGridComponentFactory: RemoteGridScreenDecomposeComponent.Factory,
    private val editorKeyFactory: KeyEditDecomposeComponent.Factory,
    private val metricApi: MetricApi
) : RemoteControlsScreenDecomposeComponent<RemoteControlsNavigationConfig>(),
    ComponentContext by componentContext {

    override val stack = childStack(
        source = navigation,
        serializer = RemoteControlsNavigationConfig.serializer(),
        initialConfiguration = RemoteControlsNavigationConfig.SelectCategory,
        handleBackButton = true,
        childFactory = ::child,
    )

    @Suppress("LongMethod")
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
                onBrandLongClick = {
                    val configuration = RemoteControlsNavigationConfig.Infrareds(
                        brandId = it,
                    )
                    navigation.pushToFront(configuration)
                },
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
                onIrFileReady = { id, name ->
                    navigation.replaceCurrent(
                        RemoteControlsNavigationConfig.ServerRemoteControl(
                            infraredFileId = id,
                            remoteName = name
                        )
                    )
                }
            )
        }

        is RemoteControlsNavigationConfig.Infrareds -> {
            infraredsScreenDecomposeComponentFactory.invoke(
                componentContext = componentContext,
                brandId = config.brandId,
                onBack = navigation::popOr,
                onRemoteFound = { id, name ->
                    navigation.replaceCurrent(
                        RemoteControlsNavigationConfig.ServerRemoteControl(
                            infraredFileId = id,
                            remoteName = name
                        )
                    )
                }
            )
        }

        is RemoteControlsNavigationConfig.Rename -> editorKeyFactory.invoke(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) },
            onSave = { savedKey ->
                if (savedKey == null) {
                    navigation.popOr(onBack::invoke)
                    return@invoke
                }
                metricApi.reportSimpleEvent(SimpleEvent.SAVE_INFRARED_LIBRARY)
                val deeplink = Deeplink.BottomBar
                    .ArchiveTab
                    .ArchiveCategory
                    .OpenSavedRemoteControl(savedKey.getKeyPath())
                onDeeplink.invoke(deeplink)
                onBack.invoke()
            },
            notSavedFlipperKey = config.notSavedFlipperKey,
            title = null
        )

        is RemoteControlsNavigationConfig.ServerRemoteControl -> remoteGridComponentFactory.invoke(
            componentContext = componentContext,
            param = ServerRemoteControlParam(
                config.infraredFileId,
                config.remoteName
            ),
            onBack = { navigation.popOr(onBack::invoke) },
            onSaveKey = {
                navigation.pushNew(RemoteControlsNavigationConfig.Rename(it))
            }
        )
    }
}
