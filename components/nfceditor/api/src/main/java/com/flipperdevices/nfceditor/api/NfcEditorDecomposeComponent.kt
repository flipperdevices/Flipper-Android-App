package com.flipperdevices.nfceditor.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

interface NfcEditorDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            flipperKeyPath: FlipperKeyPath,
            onBack: DecomposeOnBackParameter
        ): NfcEditorDecomposeComponent
    }
}
