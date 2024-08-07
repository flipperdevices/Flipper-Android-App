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
import com.flipperdevices.remotecontrols.api.GridCompositeDecomposeComponent
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.remotecontrols.impl.createcontrol.decompose.CreateControlDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.internal.LocalGridScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.internal.RemoteGridScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.model.GridNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, GridCompositeDecomposeComponent.Factory::class)
class GridCompositeDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: GridControlParam,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val editorKeyFactory: KeyEditDecomposeComponent.Factory,
    private val remoteGridComponentFactory: RemoteGridScreenDecomposeComponentImpl.Factory,
    private val localGridComponentFactory: LocalGridScreenDecomposeComponentImpl.Factory,
    private val createControlComponentFactory: CreateControlDecomposeComponentImpl.Factory,
) : GridCompositeDecomposeComponent<GridNavigationConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<GridNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = GridNavigationConfig.serializer(),
        initialConfiguration = when (param) {
            is GridControlParam.Id -> GridNavigationConfig.ServerControl(param.irFileId)
            is GridControlParam.Path -> GridNavigationConfig.SavedControl(param.flipperKeyPath)
        },
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

        is GridNavigationConfig.SavedControl -> localGridComponentFactory.invoke(
            componentContext = componentContext,
            param = GridControlParam.Path(config.keyPath),
            onBack = { navigation.popOr(onBack::invoke) },
        )

        is GridNavigationConfig.ServerControl -> remoteGridComponentFactory.invoke(
            componentContext = componentContext,
            param = GridControlParam.Id(config.id),
            onBack = { navigation.popOr(onBack::invoke) },
            onSaveKey = {
                navigation.pushNew(GridNavigationConfig.Rename(it))
            }
        )

        is GridNavigationConfig.Configuring -> createControlComponentFactory.invoke(
            componentContext = componentContext,
            savedKey = config.keyPath,
            originalKey = config.notSavedFlipperKey,
            onFinished = { navigation.replaceCurrent(GridNavigationConfig.SavedControl(it)) },
            onFailed = { navigation.popOr(onBack::invoke) }
        )
    }
}
