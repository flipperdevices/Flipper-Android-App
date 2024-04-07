package com.flipperdevices.infrared.editor.compose.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.editor.R
import com.flipperdevices.keyscreen.shared.bar.ComposableBarSimpleText
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitleWithName
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar

@Composable
internal fun ComposableInfraredEditorAppBar(
    keyName: String,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    ComposableKeyScreenAppBar(
        startBlock = {
            ComposableBarSimpleText(
                modifier = it,
                text = stringResource(id = R.string.infrared_editor_cancel),
                onClick = onCancel,
                color = LocalPallet.current.text100
            )
        },
        centerBlock = {
            ComposableBarTitleWithName(
                modifier = it,
                title = keyName,
                name = null
            )
        },
        endBlock = {
            ComposableBarSimpleText(
                modifier = it,
                text = stringResource(id = R.string.infrared_editor_save),
                onClick = onSave,
                color = LocalPallet.current.text100
            )
        }
    )
}

@Composable
@Preview
private fun PreviewComposableInfraredEditorAppBarLight() {
    FlipperThemeInternal {
        ComposableInfraredEditorAppBar(
            keyName = "Samsung TV",
            onCancel = {},
            onSave = {}
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PreviewComposableInfraredEditorAppBarDark() {
    FlipperThemeInternal {
        ComposableInfraredEditorAppBar(
            keyName = "Samsung TV",
            onCancel = {},
            onSave = {}
        )
    }
}
