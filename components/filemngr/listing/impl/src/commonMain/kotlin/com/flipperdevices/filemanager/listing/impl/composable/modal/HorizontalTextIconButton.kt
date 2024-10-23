package com.flipperdevices.filemanager.listing.impl.composable.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun HorizontalTextIconButton(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = LocalPalletV2.current.icon.blackAndWhite.default,
    textColor: Color = LocalPalletV2.current.action.blackAndWhite.text.default
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickableRipple(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painter,
            tint = iconTint,
            modifier = Modifier.size(24.dp),
            contentDescription = null
        )
        Text(
            text = text,
            style = LocalTypography.current.subtitleM12,
            color = textColor
        )
    }
}
