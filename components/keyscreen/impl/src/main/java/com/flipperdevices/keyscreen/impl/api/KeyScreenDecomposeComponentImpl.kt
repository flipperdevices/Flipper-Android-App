package com.flipperdevices.keyscreen.impl.api

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
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.keyscreen.api.KeyScreenDecomposeComponent
import com.flipperdevices.keyscreen.impl.model.KeyScreenNavigationConfig
import com.flipperdevices.nfceditor.api.NfcEditorDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class KeyScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted keyPath: FlipperKeyPath,
    private val infraredKeyScreenFactory: KeyScreenDecomposeComponent.Factory,
    private val keyScreenViewFactory: KeyScreenViewDecomposeComponentImpl.Factory,
    private val keyEditFactory: KeyEditDecomposeComponent.Factory,
    private val nfcEditFactory: NfcEditorDecomposeComponent.Factory
) : KeyScreenDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<KeyScreenNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = KeyScreenNavigationConfig.serializer(),
        initialConfiguration = KeyScreenNavigationConfig.Main(keyPath),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: KeyScreenNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is KeyScreenNavigationConfig.Main -> when {
            config.keyPath.path.keyType != FlipperKeyType.INFRARED ||
                config.keyPath.deleted ->
                keyScreenViewFactory(
                    componentContext = componentContext,
                    keyPath = config.keyPath,
                    navigation = navigation
                )

            else -> infraredKeyScreenFactory(
                componentContext = componentContext,
                keyPath = config.keyPath
            )
        }

        is KeyScreenNavigationConfig.KeyEdit -> keyEditFactory(
            componentContext = componentContext,
            onBack = navigation::pop,
            flipperKeyPath = config.keyPath,
            title = null
        )

        is KeyScreenNavigationConfig.NfcEdit -> nfcEditFactory(
            componentContext = componentContext,
            flipperKeyPath = config.keyPath
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
    @ContributesBinding(AppGraph::class, KeyScreenDecomposeComponent.Factory::class)
    fun interface Factory : KeyScreenDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath
        ): KeyScreenDecomposeComponentImpl
    }
}
