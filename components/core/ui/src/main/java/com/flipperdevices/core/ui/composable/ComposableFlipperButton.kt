package com.flipperdevices.core.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.R

@Composable
fun ComposableFlipperButton(
    modifier: Modifier = Modifier,
    text: String,
    textPadding: PaddingValues = PaddingValues(all = 16.dp),
    onClick: () -> Unit = {},
    fontSize: TextUnit = 16.sp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(size = 20.dp))
            .background(colorResource(R.color.accent_secondary))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(textPadding),
            text = text,
            color = colorResource(R.color.white_100),
            fontSize = fontSize,
            fontWeight = FontWeight.W700
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableFlipperButtonPreview() {
    Column {
        ComposableFlipperButton(text = "Test")
    }
}
