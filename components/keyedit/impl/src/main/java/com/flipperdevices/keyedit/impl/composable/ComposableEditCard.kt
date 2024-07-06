package com.flipperdevices.keyedit.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.ComposableKeyType
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyedit.impl.R
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.ComposableKeyContent

@Composable
fun ComposableEditCard(
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    name: String?,
    notes: String?,
    keyParsed: FlipperKeyParsed,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(space = 18.dp)
        ) {
            ComposableCardContent(
                onNameChange = onNameChange,
                onNoteChange = onNoteChange,
                name = name,
                notes = notes,
                keyParsed = keyParsed,
                enabled = enabled
            )
        }
    }
}

@Composable
private fun ColumnScope.ComposableCardContent(
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    name: String?,
    notes: String?,
    keyParsed: FlipperKeyParsed,
    enabled: Boolean
) {
    ComposableKeyType(keyParsed.fileType)
    FlipperTextField(
        modifier = Modifier.padding(horizontal = 12.dp),
        title = stringResource(R.string.keyedit_name_title),
        label = stringResource(R.string.keyedit_name_hint),
        text = name.orEmpty(),
        onTextChange = onNameChange,
        keyboardType = KeyboardType.Ascii,
        enabled = enabled
    )
    FlipperTextField(
        modifier = Modifier.padding(horizontal = 12.dp),
        title = stringResource(R.string.keyedit_notes_title),
        label = stringResource(R.string.keyedit_notes_hint),
        text = notes.orEmpty(),
        onTextChange = onNoteChange,
        keyboardType = KeyboardType.Text,
        enabled = enabled
    )

    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = LocalPallet.current.divider12
    )

    ComposableKeyContent(keyParsed)
}
