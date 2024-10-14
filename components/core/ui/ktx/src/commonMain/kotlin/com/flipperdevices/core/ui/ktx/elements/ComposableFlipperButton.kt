package com.flipperdevices.core.ui.ktx.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.placeholderByLocalProvider
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableFlipperButton(
    text: String,
    modifier: Modifier = Modifier,
    textPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 38.dp),
    onClick: () -> Unit = {},
    textStyle: TextStyle = TextStyle(),
    enabled: Boolean = true
) {
    val background = if (enabled) {
        LocalPallet.current.accentSecond
    } else {
        LocalPallet.current.flipperDisableButton
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(size = 30.dp))
            .placeholderByLocalProvider()
            .background(background)
            .clickableRipple(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(textPadding),
            text = text,
            color = LocalPallet.current.onFlipperButton,
            style = LocalTypography.current.buttonB16.merge(textStyle)
        )
    }
}
