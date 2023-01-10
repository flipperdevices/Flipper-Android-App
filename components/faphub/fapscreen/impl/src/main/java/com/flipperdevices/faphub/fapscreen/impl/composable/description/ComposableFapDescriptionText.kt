package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.fapscreen.impl.R

private val DEFAULT_DESCRIPTION
    get() = String((Array(size = 6) { '\n' }).toCharArray())

@Composable
internal fun ColumnScope.ComposableFapDescriptionText(
    description: String?
) {
    Text(
        modifier = Modifier.padding(bottom = 8.dp, top = 24.dp),
        text = stringResource(R.string.fapscreen_desc_title),
        style = LocalTypography.current.buttonM16,
        color = LocalPallet.current.text100
    )
    Text(
        modifier = if (description == null) {
            Modifier
                .fillMaxWidth()
                .placeholderConnecting()
        } else {
            Modifier.fillMaxWidth()
        },
        text = description ?: DEFAULT_DESCRIPTION,
        style = LocalTypography.current.bodyR14,
        color = LocalPallet.current.text100
    )
}
