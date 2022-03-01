package com.flipperdevices.keyscreen.impl.composable.view.content

import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed

@Composable
fun ComposableNFCContent(nfc: FlipperKeyParsed.NFC) {
    ComposableKeyContent(
        lines = listOf(
            FlipperFileType.NFC.humanReadableName,
            nfc.deviceType?.let { "Device type: $it" },
            nfc.uid?.let { "Uid: $it" }
        )
    )
}
