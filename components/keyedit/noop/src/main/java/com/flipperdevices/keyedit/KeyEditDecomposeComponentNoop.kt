package com.flipperdevices.keyedit

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

class KeyEditDecomposeComponentNoop(
    componentContext: ComponentContext
) : KeyEditDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() = Unit

    @ContributesBinding(AppGraph::class, KeyEditDecomposeComponent.Factory::class)
    class Factory @Inject constructor() : KeyEditDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onSave: (FlipperKey?) -> Unit,
            flipperKeyPath: FlipperKeyPath,
            title: String?
        ) = KeyEditDecomposeComponentNoop(componentContext)

        override fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onSave: (FlipperKey?) -> Unit,
            notSavedFlipperKey: NotSavedFlipperKey,
            title: String?
        ) = KeyEditDecomposeComponentNoop(componentContext)
    }
}
