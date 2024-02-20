package com.flipperdevices.infrared.impl.api

import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.infrared.api.InfraredDecomposeComponent
import com.flipperdevices.infrared.api.InfraredEditorDecomposeComponent
import com.flipperdevices.infrared.impl.model.InfraredNavigationConfig
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InfraredDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted keyPath: FlipperKeyPath,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val infraredViewFactory: InfraredViewDecomposeComponentImpl.Factory,
    private val infraredEditorFactory: InfraredEditorDecomposeComponent.Factory,
    private val editorKeyFactory: KeyEditDecomposeComponent.Factory
) : InfraredDecomposeComponent<InfraredNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<InfraredNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = InfraredNavigationConfig.serializer(),
        initialConfiguration = InfraredNavigationConfig.View(keyPath),
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
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, InfraredDecomposeComponent.Factory::class)
    interface Factory : InfraredDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath,
            onBack: DecomposeOnBackParameter
        ): InfraredDecomposeComponentImpl
    }
}
