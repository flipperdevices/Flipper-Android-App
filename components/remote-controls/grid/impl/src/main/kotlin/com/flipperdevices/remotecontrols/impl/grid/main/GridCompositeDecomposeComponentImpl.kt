package com.flipperdevices.remotecontrols.impl.grid.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.remotecontrols.api.GridCompositeDecomposeComponent
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.internal.LocalGridScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.internal.RemoteGridScreenDecomposeComponentImpl
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.model.GridNavigationConfig
import com.flipperdevices.remotecontrols.impl.grid.save.viewmodel.SaveRemoteControlViewModel
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, GridCompositeDecomposeComponent.Factory::class)
class GridCompositeDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: GridControlParam,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val createSaveRemoteControlViewModel: Provider<SaveRemoteControlViewModel>,
    private val editorKeyFactory: KeyEditDecomposeComponent.Factory,
    private val remoteGridComponentFactory: RemoteGridScreenDecomposeComponentImpl.Factory,
    private val localGridComponentFactory: LocalGridScreenDecomposeComponentImpl.Factory,
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
    private val saveRemoteControlViewModel = instanceKeeper.getOrCreate(
        key = "movefilegridcomposecomponent",
        factory = createSaveRemoteControlViewModel::get
    )

    private fun child(
        config: GridNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is GridNavigationConfig.Rename -> editorKeyFactory.invoke(
            componentContext = componentContext,
            onBack = navigation::popOr,
            onSave = { savedKey ->
                if (savedKey == null) {
                    navigation.popOr()
                    return@invoke
                }
                saveRemoteControlViewModel.moveAndUpdate(
                    savedKey = savedKey,
                    originalKey = config.notSavedFlipperKey,
                    onFinished = {
                        navigation.replaceAll(GridNavigationConfig.SavedControl(it))
                    }
                )
            },
            notSavedFlipperKey = config.notSavedFlipperKey,
            title = null
        )

        is GridNavigationConfig.SavedControl -> localGridComponentFactory.invoke(
            componentContext = componentContext,
            param = GridControlParam.Path(config.keyPath),
            onBack = onBack::invoke,
        )

        is GridNavigationConfig.ServerControl -> remoteGridComponentFactory.invoke(
            componentContext = componentContext,
            param = GridControlParam.Id(config.id),
            onBack = onBack::invoke,
            onSaveKey = {
                navigation.pushNew(GridNavigationConfig.Rename(it))
            }
        )
    }
}
