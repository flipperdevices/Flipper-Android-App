package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.R

@Composable
internal fun ColumnScope.ComposableInfraredContent(infrared: FlipperKeyParsed.Infrared) {
    ComposableKeyContent(
        lines = listOf(
            stringResource(R.string.content_infrared_data) to infrared.protocol
        )
    )
}
