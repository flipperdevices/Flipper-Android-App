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

@Composable
fun ComposableEditScreen(
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    title: String?,
    state: KeyEditState,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    when (state) {
        KeyEditState.Loading,
        is KeyEditState.Saving -> ComposableEditScreenLoading()
        is KeyEditState.Editing -> ComposableEditScreenEditing(
            onNameChange = onNameChange,
            onNoteChange = onNoteChange,
            title = title,
            state = state,
            onCancel = onBack,
            onSave = onSave
        )
        KeyEditState.Failed -> onBack()
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
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    title: String?,
    state: KeyEditState.Editing,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val buttonState = if (state.savingKeyActive) {
        SaveButtonState.ENABLED
    } else {
        SaveButtonState.DISABLED
    }
    Column {
        ComposableEditAppBar(
            title = title,
            saveButtonState = buttonState,
            onBack = onCancel,
            onSave = onSave
        )
        ComposableEditCard(
            onNameChange = onNameChange,
            onNoteChange = onNoteChange,
            name = state.name,
            notes = state.notes,
            keyParsed = state.parsedKey,
            enabled = true
        )
    }
}
