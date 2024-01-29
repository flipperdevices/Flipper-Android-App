package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.R
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.ComposableNFCContent(nfc: FlipperKeyParsed.NFC) {
    ComposableKeyContent(
        lines = persistentListOf(
            stringResource(R.string.content_nfc_device_type) to nfc.deviceType,
            stringResource(R.string.content_nfc_uid) to nfc.uid
        )
    )
}
