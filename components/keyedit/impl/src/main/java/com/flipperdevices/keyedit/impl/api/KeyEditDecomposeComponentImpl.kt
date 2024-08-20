package com.flipperdevices.keyedit.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKey
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
    @Assisted private val onSave: (FlipperKey?) -> Unit,
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
                viewModel.onSave(onSave::invoke)
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            onSave: (FlipperKey?) -> Unit,
            editableKey: EditableKey,
            title: String?
        ): KeyEditDecomposeComponentImpl
    }
}
