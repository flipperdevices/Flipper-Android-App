package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.R

@Composable
internal fun ColumnScope.ComposableIButtonContent(iButton: FlipperKeyParsed.IButton) {
    ComposableKeyContent(
        lines = listOf(
            stringResource(R.string.content_ibutton_key_type) to iButton.keyType,
            stringResource(R.string.content_ibutton_data) to iButton.data
        )
    )
}
