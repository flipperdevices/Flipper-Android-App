package com.flipperdevices.keyscreen.shared

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.content.ComposableIButtonContent
import com.flipperdevices.keyscreen.shared.content.ComposableInfraredContent
import com.flipperdevices.keyscreen.shared.content.ComposableNFCContent
import com.flipperdevices.keyscreen.shared.content.ComposableRFIDContent
import com.flipperdevices.keyscreen.shared.content.ComposableSubGhzContent
import com.flipperdevices.keyscreen.shared.content.ComposableUnrecognizedContent

@Composable
fun ColumnScope.ComposableKeyContent(keyParsed: FlipperKeyParsed) {
    when (keyParsed) {
        is FlipperKeyParsed.RFID -> ComposableRFIDContent(keyParsed)
        is FlipperKeyParsed.IButton -> ComposableIButtonContent(keyParsed)
        is FlipperKeyParsed.Infrared -> ComposableInfraredContent(keyParsed)
        is FlipperKeyParsed.NFC -> ComposableNFCContent(keyParsed)
        is FlipperKeyParsed.SubGhz -> ComposableSubGhzContent(keyParsed)
        is FlipperKeyParsed.Unrecognized -> ComposableUnrecognizedContent(keyParsed)
    }
}
