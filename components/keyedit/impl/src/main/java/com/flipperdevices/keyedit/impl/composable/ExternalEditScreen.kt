package com.flipperdevices.keyedit.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModelFactory

@Composable
fun ExternalEditScreen(
    flipperKey: FlipperKey,
    onKeyEditFinished: (FlipperKey) -> Unit,
    parsedKey: FlipperKeyParsed? = null,
    editViewModel: KeyEditViewModel = viewModel(
        key = flipperKey.path.pathToKey + flipperKey.notes,
        factory = KeyEditViewModelFactory(flipperKey, LocalContext.current, parsedKey)
    )
) {
    val editState by editViewModel.getEditState().collectAsState()
    key(editViewModel, onKeyEditFinished) { editViewModel.subscribeOnFinish(onKeyEditFinished) }

    ComposableEditScreen(
        editViewModel,
        editState,
        onCancel = editViewModel::cancel,
        onSave = editViewModel::onSave
    )
}
