package com.flipperdevices.keyscreen.impl.composable.view.content

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

@Composable
fun ComposableIButtonContent(iButton: FlipperKeyParsed.IButton) {
    ComposableKeyContent(
        lines = listOf(
            FlipperFileType.I_BUTTON.humanReadableName,
            iButton.keyType?.let { "Key type: $it" },
            iButton.data?.let { "Data: $it" }
        )
    )
}
