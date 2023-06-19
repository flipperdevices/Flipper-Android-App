package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed

@Composable
internal fun ColumnScope.ComposableUnrecognizedContent(
    unrecognizedKey: FlipperKeyParsed.Unrecognized
) {
    ComposableKeyContent(
        lines = unrecognizedKey.orderedDict
    )
}
