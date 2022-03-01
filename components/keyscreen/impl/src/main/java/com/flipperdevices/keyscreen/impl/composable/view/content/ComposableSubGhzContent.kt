package com.flipperdevices.keyscreen.impl.composable.view.content

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

@Composable
fun ComposableSubGhzContent(subghz: FlipperKeyParsed.SubGhz) {
    ComposableKeyContent(
        lines = listOf(
            FlipperFileType.SUB_GHZ.humanReadableName,
            subghz.protocol?.let { "Protocol: $it" },
            subghz.key?.let { "Key: $it" }
        )
    )
}
