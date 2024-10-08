package com.flipperdevices.filemanager.ui.components.itemcard.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ItemCardSubtitle(text: String) {
    Text(
        text = text,
        style = LocalTypography.current.subtitleM10,
        color = LocalPalletV2.current.text.label.secondary
    )
}
