package com.flipperdevices.nfceditor.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.nfceditor.api.NfcEditorDecomposeComponent
import com.flipperdevices.nfceditor.impl.model.NfcEditorNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class NfcEditorDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val flipperKeyPath: FlipperKeyPath,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val saveDecomposeComponentFactory: KeyEditDecomposeComponent.Factory,
    private val nfcEditorDecomposeComponentFactory: NfcEditorScreenDecomposeComponentImpl.Factory
) : NfcEditorDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<NfcEditorNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = NfcEditorNavigationConfig.serializer(),
        initialConfiguration = NfcEditorNavigationConfig.NfcEditor(flipperKeyPath),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: NfcEditorNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is NfcEditorNavigationConfig.NfcEditor -> nfcEditorDecomposeComponentFactory(
            componentContext = componentContext,
            flipperKeyPath = config.flipperKeyPath,
            navigation = navigation,
            onBack = onBack
        )

        is NfcEditorNavigationConfig.Save -> saveDecomposeComponentFactory(
            componentContext = componentContext,
            onBack = navigation::pop,
            notSavedFlipperKey = config.notSavedFlipperKey,
            title = config.title
        )
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val childStack by stack.subscribeAsState()

        Children(
            stack = childStack,
        ) {
            it.instance.Render()
        }
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, NfcEditorDecomposeComponent.Factory::class)
    interface Factory : NfcEditorDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            flipperKeyPath: FlipperKeyPath,
            onBack: DecomposeOnBackParameter
        ): NfcEditorDecomposeComponentImpl
    }
}
