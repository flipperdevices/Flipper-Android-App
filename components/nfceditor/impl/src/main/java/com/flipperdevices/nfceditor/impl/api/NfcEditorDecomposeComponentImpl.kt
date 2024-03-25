package com.flipperdevices.nfceditor.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.nfceditor.api.NfcEditorDecomposeComponent
import com.flipperdevices.nfceditor.impl.model.NfcEditorNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, NfcEditorDecomposeComponent.Factory::class)
class NfcEditorDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val flipperKeyPath: FlipperKeyPath,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val saveDecomposeComponentFactory: KeyEditDecomposeComponent.Factory,
    private val nfcEditorDecomposeComponentFactory: NfcEditorScreenDecomposeComponentImpl.Factory
) : NfcEditorDecomposeComponent<NfcEditorNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<NfcEditorNavigationConfig, DecomposeComponent>> = childStack(
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
            onBack = { navigation.popOr(onBack::invoke) },
            notSavedFlipperKey = config.notSavedFlipperKey,
            title = config.title
        )
    }
}
