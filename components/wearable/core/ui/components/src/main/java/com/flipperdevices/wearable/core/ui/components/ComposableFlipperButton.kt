package com.flipperdevices.wearable.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableFlipperButton(
    text: String,
    modifier: Modifier = Modifier,
    textPadding: PaddingValues = PaddingValues(
        vertical = 16.dp,
        horizontal = 38.dp
    ),
    onClick: () -> Unit = {},
    textStyle: TextStyle = TextStyle(),
    cornerRoundSize: Dp = 30.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(size = cornerRoundSize))
            .clickableRipple(onClick = onClick)
            .background(LocalPallet.current.accentSecond),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(textPadding),
            text = text,
            style = LocalTypography.current.buttonB16.merge(textStyle),
            color = LocalPallet.current.onFlipperButton
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperButtonPreview() {
    FlipperThemeInternal {
        Column {
            ComposableFlipperButton(text = "Tesвыавыаываывавыавыаываываt")
        }
    }
}
