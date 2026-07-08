package com.flipperdevices.keyedit

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

class KeyEditDecomposeComponentNoop(
    componentContext: ComponentContext
) : KeyEditDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() = Unit

    @ContributesBinding(AppGraph::class, binding<KeyEditDecomposeComponent.Factory>())
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
