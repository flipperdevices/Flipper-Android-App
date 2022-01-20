package com.flipperdevices.keyscreen.impl.composable.content

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

@Composable
fun ComposableInfraredContent(infrared: FlipperKeyParsed.Infrared) {
    ComposableKeyContent(
        lines = listOf(
            FlipperFileType.INFRARED.humanReadableName,
            infrared.protocol?.let { "Protocol: $it" },
        )
    )
}
