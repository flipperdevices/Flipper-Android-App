package com.flipperdevices.keyscreen.impl.composable.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.common.ComposableKeyType
import com.flipperdevices.keyscreen.impl.viewmodel.edit.KeyEditViewModel

@Composable
fun ComposableKeyEdit(
    viewModel: KeyEditViewModel,
    name: String?,
    notes: String?,
    keyParsed: FlipperKeyParsed
) {
    Column {
        ComposableKeyType(keyParsed.fileType)
        FlipperTextField(
            title = stringResource(R.string.keyedit_name_title),
            label = stringResource(R.string.keyedit_name_hint),
            text = name ?: "",
            onTextChange = viewModel::onNameChange,
            keyboardType = KeyboardType.Ascii
        )
    }
}
