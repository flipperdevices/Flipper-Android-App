package com.flipperdevices.filemanager.editor.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.filemanager.editor.composable.EditorAppBar
import com.flipperdevices.filemanager.editor.viewmodel.EditorViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import okio.Path

@ContributesAssistedFactory(AppGraph::class, FileManagerEditorDecomposeComponent.Factory::class)
class FileManagerEditorDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val path: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val editorViewModelFactory: EditorViewModel.Factory
) : FileManagerEditorDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
        val editorViewModel = viewModelWithFactory(path.toString()) {
            editorViewModelFactory.invoke(path)
        }
        val editorState by editorViewModel.state.collectAsState()
        Scaffold(
            topBar = {
                EditorAppBar(
                    path = path,
                    onSaveClick = {},
                    onSaveAsClick = {},
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
                        var text by remember(localEditorState.hexString) { mutableStateOf(localEditorState.hexString.content) }
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
                            value = text,
                            onValueChange = {
                                text = it
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
    }
}
