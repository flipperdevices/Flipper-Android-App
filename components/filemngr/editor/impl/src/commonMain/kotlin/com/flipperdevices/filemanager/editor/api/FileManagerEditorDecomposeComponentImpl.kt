package com.flipperdevices.filemanager.editor.api

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.editor.composable.CreateFileDialogComposable
import com.flipperdevices.filemanager.editor.composable.EditorAppBar
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
    private val fileNameViewModelProvider: Provider<FileNameViewModel>
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
        val editorState by editorViewModel.state.collectAsState()

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
        Scaffold(
            topBar = {
                EditorAppBar(
                    path = path,
                    onSaveClick = onSaveClick@{
                        val rawContent = editorViewModel.getRawContent() ?: return@onSaveClick
                        uploaderDecomposeComponent.uploadRaw(
                            folderPath = path.parent ?: return@onSaveClick,
                            fileName = path.name,
                            content = rawContent
                        )
                    },
                    onSaveAsClick = {
                        fileNameViewModel.show()
                    },
                    onBack = onBack::invoke,
                    editorEncodingEnum = (editorState as? EditorViewModel.State.Loaded)?.encoding,
                    canSave = (editorState is EditorViewModel.State.Loaded),
                    onEditorTabChange = editorViewModel::onEditorTypeChange
                )
            }
        ) { contentPadding ->
            when (val localEditorState = editorState) {
                EditorViewModel.State.Error -> Box(Modifier.fillMaxSize().background(Color.Red))
                is EditorViewModel.State.Loading -> Box(
                    Modifier.fillMaxSize().background(Color.Cyan)
                )

                is EditorViewModel.State.Saving -> Box(
                    Modifier.fillMaxSize().background(Color.Blue)
                )

                EditorViewModel.State.Preparing -> Box(
                    Modifier.fillMaxSize().background(Color.Green)
                )

                is EditorViewModel.State.Loaded -> {
                    Column {
                        if (localEditorState.isTooLarge) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LocalPallet.current.warningColor),
                                text = "The file is larger than 1MB and therefore only part of the file is shown. The content will be overwritten when you save it!",
                                color = LocalPallet.current.textOnWarningBackground
                            )
                        }
                        TextField(
                            modifier = Modifier.fillMaxSize(),
                            value = localEditorState.hexString.content,
                            onValueChange = {
                                editorViewModel.onTextChanged(it)
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                cursorColor = LocalPallet.current.text100
                            )
                        )
                    }
                }

                EditorViewModel.State.Saved -> Box(Modifier.fillMaxSize().background(Color.Yellow))
            }
        }
        val uploaderState by uploaderDecomposeComponent.state.collectAsState()
        AnimatedVisibility(
            visible = uploaderState is UploaderDecomposeComponent.State.Uploading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            val speedState by uploaderDecomposeComponent.speedState.collectAsState(null)
            uploaderDecomposeComponent.Render(
                state = uploaderState,
                speedState = speedState,
                onCancel = uploaderDecomposeComponent::onCancel,
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalPalletV2.current.surface.backgroundMain.body)
                    .navigationBarsPadding()
                    .systemBarsPadding(),
            )
        }
    }
}
