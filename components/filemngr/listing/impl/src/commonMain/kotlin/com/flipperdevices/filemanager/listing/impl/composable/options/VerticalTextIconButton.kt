package com.flipperdevices.filemanager.listing.impl.composable.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun VerticalTextIconButton(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = LocalPalletV2.current.icon.blackAndWhite.default,
    textColor: Color = LocalPalletV2.current.action.blackAndWhite.text.default
) {
    Column(
        modifier = modifier
            .clickableRipple(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
