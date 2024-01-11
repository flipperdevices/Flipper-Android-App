package com.flipperdevices.filemanager.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.filemanager.impl.composable.ComposableFileManagerEditorScreen
import com.flipperdevices.filemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.filemanager.impl.viewmodels.EditorViewModel
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileManagerEditingComponent @AssistedInject constructor(
    private val editorViewModelFactory: EditorViewModel.Factory,
    @Assisted componentContext: ComponentContext,
    @Assisted val config: FileManagerNavigationConfig.Editing,
    @Assisted val navigation: StackNavigation<FileManagerNavigationConfig>
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
            onBack = navigation::pop
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            config: FileManagerNavigationConfig.Editing,
            navigation: StackNavigation<FileManagerNavigationConfig>
        ): FileManagerEditingComponent
    }
}
