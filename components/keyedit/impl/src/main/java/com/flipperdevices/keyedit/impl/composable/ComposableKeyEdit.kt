package com.flipperdevices.keyedit.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyedit.impl.R
import com.flipperdevices.keyscreen.shared.ComposableKeyType

@Composable
fun ComposableKeyEdit(
    viewModel: com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel,
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
