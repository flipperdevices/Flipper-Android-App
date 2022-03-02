package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.R

@Composable
internal fun ColumnScope.ComposableNFCContent(nfc: FlipperKeyParsed.NFC) {
    ComposableKeyContent(
        lines = listOf(
            stringResource(R.string.content_nfc_device_type) to nfc.deviceType,
            stringResource(R.string.content_nfc_uid) to nfc.uid
        )
    )
}
