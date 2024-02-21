package com.flipperdevices.keyedit.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.keyedit.api.KeyEditDecomposeComponent
import com.flipperdevices.keyedit.impl.composable.ComposableEditScreen
import com.flipperdevices.keyedit.impl.model.EditableKey
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class KeyEditDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val editableKey: EditableKey,
    @Assisted private val title: String?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val keyEditViewModelFactory: KeyEditViewModel.Factory
) : KeyEditDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val viewModel: KeyEditViewModel = viewModelWithFactory(key = editableKey.toString()) {
            keyEditViewModelFactory.invoke(editableKey)
        }
        val state by viewModel.getEditState().collectAsState()
        ComposableEditScreen(
            onNameChange = viewModel::onNameChange,
            onNoteChange = viewModel::onNotesChange,
            title = title,
            state = state,
            onBack = onBack::invoke,
            onSave = {
                viewModel.onSave(onBack::invoke)
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            editableKey: EditableKey,
            title: String?
        ): KeyEditDecomposeComponentImpl
    }
}
