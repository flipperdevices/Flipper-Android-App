package com.flipperdevices.keyedit.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.keyedit.impl.model.KeyEditState
import com.flipperdevices.keyedit.impl.model.SaveButtonState
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel

@Composable
fun ComposableEditScreen(
    viewModel: KeyEditViewModel,
    title: String?,
    state: KeyEditState,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    when (state) {
        KeyEditState.Loading,
        is KeyEditState.Saving -> ComposableEditScreenLoading()
        is KeyEditState.Editing -> ComposableEditScreenEditing(
            viewModel,
            title,
            state,
            onCancel,
            onSave
        )
        KeyEditState.Failed -> LocalRouter.current.exit()
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
    title: String?,
    state: KeyEditState.Editing,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val buttonState = if (state.savingKeyActive) {
        SaveButtonState.ENABLED
    } else SaveButtonState.DISABLED
    Column {
        ComposableEditAppBar(title, buttonState, onCancel, onSave)
        ComposableEditCard(
            viewModel,
            state.name,
            state.notes,
            state.parsedKey,
            enabled = true
        )
    }
}
