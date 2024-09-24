package com.flipperdevices.filemanager.ui.components.itemcard.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ItemCardTitle(text: String) {
    Text(
        text = text,
        style = LocalTypography.current.bodyM14,
        color = LocalPalletV2.current.text.body.primary
    )
}
