package com.flipperdevices.filemanager.editor.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.filemanager.editor.composable.FileManagerEditorComposable
import com.flipperdevices.filemanager.editor.composable.content.RenderLoadingScreen
import com.flipperdevices.filemanager.editor.composable.dialog.CreateFileDialogComposable
import com.flipperdevices.filemanager.editor.viewmodel.EditorViewModel
import com.flipperdevices.filemanager.editor.viewmodel.FileNameViewModel
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, FileManagerEditorDecomposeComponent.Factory::class)
class FileManagerEditorDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val editorViewModelFactory: EditorViewModel.Factory,
    uploaderDecomposeComponentFactory: UploaderDecomposeComponent.Factory,
    private val fileNameViewModelProvider: Provider<FileNameViewModel>,
) : FileManagerEditorDecomposeComponent(componentContext) {
    private val uploaderDecomposeComponent = uploaderDecomposeComponentFactory.invoke(
        componentContext = childContext("file_editor_$path")
    )

    @Composable
    override fun Render() {
        val fileNameViewModel = viewModelWithFactory(null) {
            fileNameViewModelProvider.get()
        }
        val editorViewModel = viewModelWithFactory(path.toString()) {
            editorViewModelFactory.invoke(path)
        }

        CreateFileDialogComposable(
            fileNameViewModel = fileNameViewModel,
            onFinish = onSaveClick@{ fileName ->
                val rawContent = editorViewModel.getRawContent() ?: return@onSaveClick
                uploaderDecomposeComponent.uploadRaw(
                    folderPath = path.parent ?: return@onSaveClick,
                    fileName = fileName,
                    content = rawContent
                )
            }
        )

        FileManagerEditorComposable(
            path = path,
            editorViewModel = editorViewModel,
            uploaderDecomposeComponent = uploaderDecomposeComponent,
            fileNameViewModel = fileNameViewModel,
            onBack = onBack::invoke
        )

        uploaderDecomposeComponent.RenderLoadingScreen()
    }
}
