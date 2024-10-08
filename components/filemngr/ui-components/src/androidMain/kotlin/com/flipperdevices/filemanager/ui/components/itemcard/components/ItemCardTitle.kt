package com.flipperdevices.filemanager.ui.components.itemcard.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
internal fun ItemCardTitle(text: String) {
    Text(
        text = text,
        style = LocalTypography.current.bodyM14,
        color = LocalPalletV2.current.text.body.primary,
        maxLines = 1,
        modifier = Modifier.basicMarquee(),
        overflow = TextOverflow.Clip
    )
}
