package com.flipperdevices.keyscreen.impl.composable.content

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

@Composable
fun ComposableRFIDContent(rfid: FlipperKeyParsed.RFID) {
    ComposableKeyContent(
        lines = listOf(
            FlipperFileType.RFID.humanReadableName,
            rfid.keyType?.let { "Key type: $it" },
            rfid.data?.let { "Data: $it" }
        )
    )
}
