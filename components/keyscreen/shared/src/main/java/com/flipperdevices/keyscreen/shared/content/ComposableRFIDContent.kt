package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.R
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.ComposableRFIDContent(rfid: FlipperKeyParsed.RFID) {
    ComposableKeyContent(
        lines = persistentListOf(
            stringResource(R.string.content_rfid_key_type) to rfid.keyType,
            stringResource(R.string.content_rfid_data) to rfid.data
        )
    )
}
