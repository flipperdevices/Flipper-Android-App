package com.flipperdevices.nfceditor.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class NfcEditorDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            flipperKeyPath: FlipperKeyPath,
            onBack: DecomposeOnBackParameter
        ): NfcEditorDecomposeComponent<*>
    }
}
