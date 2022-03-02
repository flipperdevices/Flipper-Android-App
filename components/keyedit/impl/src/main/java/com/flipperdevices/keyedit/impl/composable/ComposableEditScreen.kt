package com.flipperdevices.keyedit.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.keyedit.impl.model.KeyEditState
import com.flipperdevices.keyedit.impl.model.SaveButtonState
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel

@Composable
fun ComposableEditScreen(
    viewModel: KeyEditViewModel,
    state: KeyEditState,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    when (state) {
        KeyEditState.Loading -> ComposableEditScreenLoading()
        is KeyEditState.Editing -> ComposableEditScreenEditing(viewModel, state, onCancel, onSave)
        is KeyEditState.Saving -> ComposableEditScreenSaving(viewModel, state, onCancel)
    }
}

@Composable
private fun ComposableEditScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ComposableEditScreenEditing(
    viewModel: KeyEditViewModel,
    state: KeyEditState.Editing,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val buttonState = if (state.savingKeyActive) {
        SaveButtonState.ENABLED
    } else SaveButtonState.DISABLED
    Column {
        ComposableEditAppBar(buttonState, onCancel, onSave)
        ComposableEditCard(
            viewModel,
            state.name,
            state.notes,
            state.parsedKey,
            enabled = true
        )
    }
}

@Composable
private fun ComposableEditScreenSaving(
    viewModel: KeyEditViewModel,
    state: KeyEditState.Saving,
    onCancel: () -> Unit
) {
    Column {
        ComposableEditAppBar(SaveButtonState.IN_PROGRESS, onBack = onCancel)
        ComposableEditCard(
            viewModel,
            state.name,
            state.notes,
            state.parsedKey,
            enabled = false
        )
    }
}
