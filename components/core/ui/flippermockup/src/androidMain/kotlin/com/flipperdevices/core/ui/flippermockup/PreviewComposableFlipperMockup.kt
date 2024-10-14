package com.flipperdevices.core.ui.flippermockup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Preview(
    showBackground = true,
    heightDp = 1500
)
@Composable
private fun PreviewComposableFlipperMockup() {
    FlipperThemeInternal {
        Column {
            listOf(
                HardwareColor.WHITE,
                HardwareColor.BLACK,
                HardwareColor.TRANSPARENT,
                HardwareColor.fromValue(-1)
            ).forEach { color ->
                listOf(true, false).forEach { isActive ->
                    ComposableFlipperMockup(
                        flipperColor = color,
                        isActive = isActive,
                        mockupImage = ComposableFlipperMockupImage.DEFAULT,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
