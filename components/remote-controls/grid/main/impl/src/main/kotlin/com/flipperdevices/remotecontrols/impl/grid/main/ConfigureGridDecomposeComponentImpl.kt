package com.flipperdevices.remotecontrols.impl.grid.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.remotecontrols.api.ConfigureGridDecomposeComponent
import com.flipperdevices.remotecontrols.api.CreateControlDecomposeComponent
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.remotecontrols.grid.remote.api.RemoteGridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.main.model.GridNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, ConfigureGridDecomposeComponent.Factory::class)
class ConfigureGridDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: ServerRemoteControlParam,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val editorKeyFactory: KeyEditDecomposeComponent.Factory,
    private val remoteGridComponentFactory: RemoteGridScreenDecomposeComponent.Factory,
    private val createControlComponentFactory: CreateControlDecomposeComponent.Factory,
) : ConfigureGridDecomposeComponent<GridNavigationConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<GridNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = GridNavigationConfig.serializer(),
        initialConfiguration = GridNavigationConfig.ServerControl(
            id = param.infraredFileId,
            remoteName = param.remoteName
        ),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: GridNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is GridNavigationConfig.Rename -> editorKeyFactory.invoke(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) },
            onSave = { savedKey ->
                if (savedKey == null) {
                    navigation.popOr(onBack::invoke)
                    return@invoke
                }
                navigation.pop()
                navigation.replaceCurrent(
                    GridNavigationConfig.Configuring(
                        savedKey.getKeyPath(),
                        config.notSavedFlipperKey
                    )
                )
            },
            notSavedFlipperKey = config.notSavedFlipperKey,
            title = null
        )

        is GridNavigationConfig.ServerControl -> remoteGridComponentFactory.invoke(
            componentContext = componentContext,
            param = ServerRemoteControlParam(
                config.id,
                config.remoteName
            ),
            onBack = { navigation.popOr(onBack::invoke) },
            onSaveKey = {
                navigation.pushNew(GridNavigationConfig.Rename(it))
            }
        )

        is GridNavigationConfig.Configuring -> createControlComponentFactory.invoke(
            componentContext = componentContext,
            savedKey = config.keyPath,
            originalKey = config.notSavedFlipperKey,
            onBack = onBack
        )
    }
}
