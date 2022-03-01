package com.flipperdevices.keyscreen.impl.composable.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.keyscreen.impl.model.KeyEditState
import com.flipperdevices.keyscreen.impl.viewmodel.edit.KeyEditViewModel

@Composable
fun ComposableEditScreen(
    viewModel: KeyEditViewModel,
    state: KeyEditState
) {
    when (state) {
        KeyEditState.Loading -> ComposableEditScreenLoading()
        is KeyEditState.Editing -> ComposableKeyEdit(
            viewModel,
            state.name,
            state.notes,
            state.parsedKey
        )
        KeyEditState.Finished -> return
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
