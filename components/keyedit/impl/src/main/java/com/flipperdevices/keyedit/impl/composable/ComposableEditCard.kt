package com.flipperdevices.keyedit.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.R as DesignSystem
import com.flipperdevices.keyedit.impl.R
import com.flipperdevices.keyedit.impl.viewmodel.KeyEditViewModel
import com.flipperdevices.keyscreen.shared.ComposableKeyContent
import com.flipperdevices.keyscreen.shared.ComposableKeyType

@Composable
fun ComposableEditCard(
    viewModel: KeyEditViewModel,
    name: String?,
    notes: String?,
    keyParsed: FlipperKeyParsed
) {
    Card(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp),
        shape = RoundedCornerShape(size = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(space = 18.dp)
        ) {
            ComposableCardContent(viewModel, name, notes, keyParsed)
        }
    }
}

@Composable
private fun ColumnScope.ComposableCardContent(
    viewModel: KeyEditViewModel,
    name: String?,
    notes: String?,
    keyParsed: FlipperKeyParsed
) {
    ComposableKeyType(keyParsed.fileType)
    FlipperTextField(
        modifier = Modifier.padding(horizontal = 12.dp),
        title = stringResource(R.string.keyedit_name_title),
        label = stringResource(R.string.keyedit_name_hint),
        text = name ?: "",
        onTextChange = viewModel::onNameChange,
        keyboardType = KeyboardType.Ascii
    )
    FlipperTextField(
        modifier = Modifier.padding(horizontal = 12.dp),
        title = stringResource(R.string.keyedit_notes_title),
        label = null,
        text = notes ?: "",
        onTextChange = viewModel::onNotesChange,
        keyboardType = KeyboardType.Text
    )

    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = colorResource(DesignSystem.color.black_12)
    )

    ComposableKeyContent(keyParsed)
}
