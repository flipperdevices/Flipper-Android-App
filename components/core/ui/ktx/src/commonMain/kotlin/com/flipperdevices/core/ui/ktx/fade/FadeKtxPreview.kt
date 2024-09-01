package com.flipperdevices.core.ui.ktx.fade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun FadePreviewColumn(
    itemsAmount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        repeat(itemsAmount) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Red)
            )
        }
    }
}

@Preview
@Composable
private fun TopFadePreview() {
    FlipperThemeInternal {
        FadePreviewColumn(
            modifier = Modifier.fadingEdge(FadeOrientation.Top),
            itemsAmount = 4
        )
    }
}

@Preview
@Composable
private fun BottomFadePreview() {
    FlipperThemeInternal {
        FadePreviewColumn(
            modifier = Modifier.fadingEdge(FadeOrientation.Bottom),
            itemsAmount = 4
        )
    }
}
