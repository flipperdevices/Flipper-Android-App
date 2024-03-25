package com.flipperdevices.keyscreen.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.infrared.api.InfraredDecomposeComponent
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.keyscreen.api.KeyScreenDecomposeComponent
import com.flipperdevices.keyscreen.impl.model.KeyScreenNavigationConfig
import com.flipperdevices.nfceditor.api.NfcEditorDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, KeyScreenDecomposeComponent.Factory::class)
@Suppress("LongParameterList")
class KeyScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted keyPath: FlipperKeyPath,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val infraredKeyScreenFactory: InfraredDecomposeComponent.Factory,
    private val keyScreenViewFactory: KeyScreenViewDecomposeComponentImpl.Factory,
    private val keyEditFactory: KeyEditDecomposeComponent.Factory,
    private val nfcEditFactory: NfcEditorDecomposeComponent.Factory
) : KeyScreenDecomposeComponent<KeyScreenNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<KeyScreenNavigationConfig, DecomposeComponent>> = childStack(
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
                    navigation = navigation,
                    onBack = this::internalOnBack
                )

            else -> infraredKeyScreenFactory(
                componentContext = componentContext,
                keyPath = config.keyPath,
                onBack = this::internalOnBack
            )
        }

        is KeyScreenNavigationConfig.KeyEdit -> keyEditFactory(
            componentContext = componentContext,
            onBack = this::internalOnBack,
            flipperKeyPath = config.keyPath,
            title = null
        )

        is KeyScreenNavigationConfig.NfcEdit -> nfcEditFactory(
            componentContext = componentContext,
            flipperKeyPath = config.keyPath,
            onBack = this::internalOnBack
        )
    }

    private fun internalOnBack() = navigation.popOr(onBack::invoke)
}
