package com.flipperdevices.newfilemanager.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.newfilemanager.impl.composable.ComposableFileManagerEditorScreen
import com.flipperdevices.newfilemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.newfilemanager.impl.viewmodels.EditorViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileManagerEditingComponent @AssistedInject constructor(
    private val editorViewModelFactory: EditorViewModel.Factory,
    @Assisted componentContext: ComponentContext,
    @Assisted private val config: FileManagerNavigationConfig.Editing,
    @Assisted private val onBack: DecomposeOnBackParameter
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val editorViewModel = viewModelWithFactory(key = config.shareFile.toString()) {
            editorViewModelFactory(config.shareFile)
        }
        val editorState by editorViewModel.getEditorState().collectAsState()
        ComposableFileManagerEditorScreen(
            editorState = editorState,
            onClickSaveButton = editorViewModel::onSaveFile,
            onBack = onBack::invoke
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            config: FileManagerNavigationConfig.Editing,
            onBack: DecomposeOnBackParameter
        ): FileManagerEditingComponent
    }
}
