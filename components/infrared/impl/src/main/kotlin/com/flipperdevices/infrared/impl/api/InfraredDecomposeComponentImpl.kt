package com.flipperdevices.infrared.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.infrared.api.InfraredDecomposeComponent
import com.flipperdevices.infrared.api.InfraredEditorDecomposeComponent
import com.flipperdevices.infrared.impl.model.InfraredNavigationConfig
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.local.api.LocalGridScreenDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, InfraredDecomposeComponent.Factory::class)
class InfraredDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val keyPath: FlipperKeyPath,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val infraredViewFactory: InfraredViewDecomposeComponentImpl.Factory,
    private val infraredEditorFactory: InfraredEditorDecomposeComponent.Factory,
    private val editorKeyFactory: KeyEditDecomposeComponent.Factory,
    private val savedGridFactory: LocalGridScreenDecomposeComponent.Factory
) : InfraredDecomposeComponent<InfraredNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<InfraredNavigationConfig, DecomposeComponent>> =
        childStack(
            source = navigation,
            serializer = InfraredNavigationConfig.serializer(),
            initialConfiguration = InfraredNavigationConfig.RemoteControl(keyPath),
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(
        config: InfraredNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is InfraredNavigationConfig.View -> infraredViewFactory(
            componentContext = componentContext,
            keyPath = config.keyPath,
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is InfraredNavigationConfig.Edit -> infraredEditorFactory(
            componentContext = componentContext,
            keyPath = config.keyPath,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is InfraredNavigationConfig.Rename -> editorKeyFactory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) },
            flipperKeyPath = config.keyPath,
            title = null
        )

        is InfraredNavigationConfig.RemoteControl -> savedGridFactory.invoke(
            componentContext = componentContext,
            keyPath = keyPath,
            onBack = { navigation.popOr(onBack::invoke) },
            onCallback = {
                when (it) {
                    LocalGridScreenDecomposeComponent.Callback.UiFileNotFound -> {
                        navigation.replaceCurrent(InfraredNavigationConfig.View(config.keyPath))
                    }
                    is LocalGridScreenDecomposeComponent.Callback.ViewRemoteInfo -> {
                        navigation.replaceCurrent(InfraredNavigationConfig.View(it.keyPath))
                    }

                    is LocalGridScreenDecomposeComponent.Callback.Rename -> {
                        navigation.pushNew(InfraredNavigationConfig.Rename(it.keyPath))
                    }

                    LocalGridScreenDecomposeComponent.Callback.Deleted -> navigation.popOr(onBack::invoke)
                }
            }
        )
    }
}
