package com.flipperdevices.infrared.editor.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.infrared.editor.composable.components.ComposableEditorInfraredControls
import com.flipperdevices.infrared.editor.composable.components.ComposableInfraredDialog
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import com.flipperdevices.infrared.editor.viewmodel.InfraredEditorViewModel
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableEditorInfrared(
    onCancel: () -> Unit,
    keyEmulateUiApi: KeyEmulateUiApi,
    viewModel: InfraredEditorViewModel = tangleViewModel()
) {
    val state by viewModel.getInfraredControlState().collectAsState()
    val stateDialog by viewModel.getShowOnSaveDialogState().collectAsState()

    when (val localState = state) {
        InfraredEditorState.Error -> {
            LaunchedEffect(key1 = Unit) { onCancel() }
        }
        InfraredEditorState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
        is InfraredEditorState.LoadedKey -> ComposableEditorInfraredControls(
            keyEmulateUiApi = keyEmulateUiApi,
            onCancel = { viewModel.onCancel(onCancel) },
            state = localState,
            onSave = { viewModel.onSave(onCancel) },
            onChangePosition = viewModel::onChangePosition
        )
    }

    ComposableInfraredDialog(
        state = stateDialog,
        onSave = { viewModel.onSave(onCancel) },
        onNotSave = onCancel,
        onDismiss = viewModel::dismissDialog
    )

    BackHandler {
        viewModel.onCancel(onCancel)
    }
}
