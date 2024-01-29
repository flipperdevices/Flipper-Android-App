package com.flipperdevices.keyscreen.shared.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.keyscreen.shared.R
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ColumnScope.ComposableSubGhzContent(subghz: FlipperKeyParsed.SubGhz) {
    ComposableKeyContent(
        lines = persistentListOf(
            stringResource(R.string.content_subghz_protocol) to subghz.protocol,
            stringResource(R.string.content_subghz_key) to subghz.key
        )
    )
}
