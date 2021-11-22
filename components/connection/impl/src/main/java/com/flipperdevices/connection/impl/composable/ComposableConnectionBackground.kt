package com.flipperdevices.connection.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val CORNER_RADIUS = 14.dp

@Composable
fun ComposableConnectionBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentSize(align = Alignment.Center)
            .clip(RoundedCornerShape(CORNER_RADIUS))
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary,
                        MaterialTheme.colors.primaryVariant
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(all = 2.dp)
                .clip(RoundedCornerShape(CORNER_RADIUS))
                .background(Color.White)
        ) {
            Box(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
                content()
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun PreviewComposableConnectionBackground() {
    ComposableConnectionBackground {
        Text(text = "Sample")
    }
}
