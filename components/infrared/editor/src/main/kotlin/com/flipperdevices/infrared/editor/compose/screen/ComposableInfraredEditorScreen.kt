package com.flipperdevices.infrared.editor.compose.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import com.flipperdevices.infrared.editor.viewmodel.InfraredViewModel
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenError
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenLoading
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableInfraredEditorScreen(
    onBack: () -> Unit,
    viewModel: InfraredViewModel = tangleViewModel()
) {
    val keyState by viewModel.getKeyState().collectAsState()
    val dialogState by viewModel.getDialogState().collectAsState()

    BackHandler {
        viewModel.processCancel(onBack)
    }

    when (val localState = keyState) {
        InfraredEditorState.InProgress -> ComposableKeyScreenLoading()
        is InfraredEditorState.Error -> ComposableKeyScreenError(
            text = stringResource(id = localState.reason)
        )
        is InfraredEditorState.Ready ->
            ComposableInfraredEditorScreenReady(
                keyState = localState,
                dialogState = dialogState,
                onDoNotSave = onBack,
                onDismissDialog = viewModel::onDismissDialog,
                onCancel = { viewModel.processCancel(onBack) },
                onSave = { viewModel.processSave(onBack) },
                onChangeName = { index, value ->
                    viewModel.editRemoteName(index, value)
                },
                onDelete = viewModel::processDeleteRemote,
                onEditOrder = viewModel::processEditOrder,
                onChangeIndexEditor = viewModel::processChangeIndexEditor
            )
    }
}
