package com.flipperdevices.filemanager.editor.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import okio.Path

abstract class FileManagerEditorDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onFileChanged: (ListingItem) -> Unit,
            path: Path
        ): FileManagerEditorDecomposeComponent<*>
    }
}
