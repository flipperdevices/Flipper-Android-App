package com.flipperdevices.wearable.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
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
    Button(
        modifier = modifier
            .clip(RoundedCornerShape(size = cornerRoundSize)),
        onClick = onClick,
        colors = ButtonDefaults.primaryButtonColors(
            backgroundColor = LocalPallet.current.accentSecond,
            contentColor = LocalPallet.current.onFlipperButton
        )
    ) {
        Text(
            modifier = Modifier.padding(textPadding),
            text = text,
            style = LocalTypography.current.buttonB16.merge(textStyle)
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperButtonPreview() {
    Column {
        ComposableFlipperButton(text = "Test")
    }
}
