package com.flipperdevices.filemanager.listing.impl.composable.options

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun VerticalTextIconButton(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    iconTint: Color = LocalPalletV2.current.icon.blackAndWhite.default,
    iconDisabledTint: Color = LocalPalletV2.current.action.blackAndWhite.icon.disabled,
    textColor: Color = LocalPalletV2.current.action.blackAndWhite.text.default,
    textDisabledColor: Color = LocalPalletV2.current.action.blackAndWhite.text.disabled
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickableRipple(onClick = { if (isEnabled) onClick.invoke() })
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painter,
            tint = animateColorAsState(if (isEnabled) iconTint else iconDisabledTint).value,
            modifier = Modifier.size(24.dp),
            contentDescription = null
        )
        Text(
            text = text,
            style = LocalTypography.current.subtitleM12,
            color = animateColorAsState(if (isEnabled) textColor else textDisabledColor).value
        )
    }
}
