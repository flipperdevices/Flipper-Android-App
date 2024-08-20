package com.flipperdevices.keyedit.impl.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, KeyEditDecomposeComponent.Factory::class)
class KeyEditDecomposeComponentFactory @Inject constructor(
    private val keyEditRealFactory: KeyEditDecomposeComponentImpl.Factory
) : KeyEditDecomposeComponent.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onBack: DecomposeOnBackParameter,
        onSave: (FlipperKey?) -> Unit,
        flipperKeyPath: FlipperKeyPath,
        title: String?
    ): KeyEditDecomposeComponent {
        val editableKey = EditableKey.Existed(flipperKeyPath)
        return keyEditRealFactory(componentContext, onBack, onSave, editableKey, title)
    }

    override fun invoke(
        componentContext: ComponentContext,
        onBack: DecomposeOnBackParameter,
        onSave: (FlipperKey?) -> Unit,
        notSavedFlipperKey: NotSavedFlipperKey,
        title: String?
    ): KeyEditDecomposeComponent {
        val editableKey = EditableKey.Limb(notSavedFlipperKey)
        return keyEditRealFactory(componentContext, onBack, onSave, editableKey, title)
    }
}
