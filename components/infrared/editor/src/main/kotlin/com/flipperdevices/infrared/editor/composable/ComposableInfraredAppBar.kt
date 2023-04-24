package com.flipperdevices.infrared.editor.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.editor.R
import com.flipperdevices.keyscreen.shared.bar.ComposableBarSimpleText
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitleWithName
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar

@Composable
internal fun ComposableInfraredAppBar(
    name: String,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ComposableKeyScreenAppBar(
        modifier = modifier,
        startBlock = {
            ComposableBarSimpleText(
                modifier = it,
                text = stringResource(R.string.infrared_editor_cancel),
                onClick = onCancel,
                color = LocalPallet.current.text100
            )
        },
        centerBlock = {
            ComposableBarTitleWithName(
                modifier = it,
                title = stringResource(R.string.infrared_editor_edit_remote),
                name = name,
            )
        },
        endBlock = {
            ComposableBarSimpleText(
                modifier = it,
                text = stringResource(R.string.infrared_editor_save),
                onClick = onSave,
                color = LocalPallet.current.accentSecond
            )
        }
    )
}

